package com.payment.kientv84.services.serviceImpls;

import com.payment.kientv84.commons.PaymentStatus;
import com.payment.kientv84.dtos.requests.PaymentUpdateRequest;
import com.payment.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.entities.PaymentEntity;
import com.payment.kientv84.entities.PaymentMethodEntity;
import com.payment.kientv84.exceptions.EnumError;
import com.payment.kientv84.exceptions.ServiceException;
import com.payment.kientv84.mappers.PaymentMapper;
import com.payment.kientv84.messaging.producer.PaymentProducer;
import com.payment.kientv84.processors.PaymentProcessor;
import com.payment.kientv84.processors.PaymentProcessorFactory;
import com.payment.kientv84.repositories.PaymentMethodRepository;
import com.payment.kientv84.repositories.PaymentRepository;
import com.payment.kientv84.services.PaymentService;
import com.payment.kientv84.ultis.KafkaObjectError;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentProcessorFactory paymentProcessorFactory;
    private final PaymentProducer paymentProducer;
    private final PaymentMapper paymentMapper;

    @Override
    public List<PaymentResponse> getAllPayment() {
        try {
            List<PaymentResponse> responses = paymentRepository.findAll().stream().map(pay -> paymentMapper.mapToPaymentResponse(pay)).toList();

            return responses;
        } catch (Exception e) {
            throw new ServiceException(EnumError.PAYMENT_GET_ERROR, "payment.get.error");
        }
    }

    @Override
    public PaymentResponse getPaymentById(UUID id) {
        try {
            PaymentEntity payment = paymentRepository.findById(id).orElseThrow(()-> new ServiceException(EnumError.PAYMENT_GET_ERROR, "payment.get.error"));

            return paymentMapper.mapToPaymentResponse(payment);

        } catch ( ServiceException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void processPayment(KafkaOrderResponse order) {
        log.info("Consume message from order service success! {}", order);
        try {
            Optional<PaymentEntity> paymentCheck = paymentRepository.findByOrderId(order.getId());
            if ( paymentCheck.isPresent() ) {
                throw new ServiceException(EnumError.PAYMENT_DATA_EXISTED, "payment.data.exit");
            }

            PaymentMethodEntity findPaymentMethodFromOrder = paymentMethodRepository.findById(order.getPaymentMethod()).orElseThrow(() -> new ServiceException(EnumError.PAYMENT_METHOD_GET_ERROR));

            // init payment
            PaymentEntity payment = PaymentEntity.builder()
                    .orderId(order.getId())
                    .userId(order.getUserId())
                    .orderCode(order.getOrderCode())
                    .amount(order.getTotalPrice())
                    .paymentMethod(findPaymentMethodFromOrder)
                    .status(PaymentStatus.PENDING)
                    .build();

            paymentRepository.save(payment);

            // Linking processor -- logic with cod, momo, paypal, ...
            PaymentProcessor processor = paymentProcessorFactory.getProcessor(findPaymentMethodFromOrder.getName());
            PaymentResponse response = processor.process(payment);

            // Cập nhật payment status trong DB (sau khi processor xử lý)
            PaymentStatus newStatus = PaymentStatus.valueOf(response.getStatus());
            payment.setStatus(newStatus);
            paymentRepository.save(payment);

            switch (newStatus) {
                case PAID:
                    log.info("Payment PAID! Producing Kafka success event...");
                    paymentProducer.producePaymentEventSuccess(paymentMapper.mapToKafkaPaymentResponse(response));
                    break;
                case COD_PENDING:
                    log.info("Payment COD_PENDING! Producing Kafka COD_PENDING event...");
                    paymentProducer.producePaymentEventCodePending(paymentMapper.mapToKafkaPaymentResponse(response));
                    break;
                default:
                    log.warn("Payment not completed: {}", newStatus);
                    paymentProducer.producePaymentEventFailed(paymentMapper.mapToKafkaPaymentResponse(response));
                    break;
            }

            log.info("Payment response processed: {}", response);

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {

            // Produce error message
            log.error("[createOrder] Error: {}", e.getMessage(), e);
            KafkaObjectError kafkaObjectError = new KafkaObjectError("PAYMENT-KAFKA-ERROR", null, e.getMessage());
            paymentProducer.produceMessageError(kafkaObjectError);

            throw new ServiceException(EnumError.INTERNAL_ERROR,  "sys.internal.error");
        }
    }

    @Override
    public PaymentResponse getPaymentByOrderId(UUID orderId) {
        return null;
    }

    @Override
    public PaymentResponse updatePaymentStatus(UUID orderId, PaymentUpdateRequest updateRequest) {
        log.info("Update payment from order service ...");
        try {
            PaymentEntity payment = paymentRepository.findByOrderId(orderId).orElseThrow(null);

            if (payment.getStatus() == PaymentStatus.PAID) {
                log.info("Exited status PAID at payment service...");
            }
            payment.setStatus(PaymentStatus.valueOf(updateRequest.getStatus()));

            paymentRepository.save(payment);

            return paymentMapper.mapToPaymentResponse(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendPaymentSuccessEvent(PaymentResponse paymentResponse) {

    }
}
