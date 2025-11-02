package com.payment.kientv84.services.serviceImpls;

import com.payment.kientv84.commons.PaymentStatus;
import com.payment.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.entities.PaymentEntity;
import com.payment.kientv84.entities.PaymentMethodEntity;
import com.payment.kientv84.exceptions.EnumError;
import com.payment.kientv84.exceptions.ServiceException;
import com.payment.kientv84.messagsing.producer.PaymentProducer;
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

            // Linking processor
            PaymentProcessor processor = paymentProcessorFactory.getProcessor(findPaymentMethodFromOrder.getName());
            PaymentResponse response = processor.process(payment);

            // Cập nhật payment status trong DB (sau khi processor xử lý)
            payment.setStatus(PaymentStatus.valueOf(response.getStatus()));
            paymentRepository.save(payment);

            // Produce Kafka event nếu SUCCESS
            if (PaymentStatus.SUCCESS.name().equalsIgnoreCase(response.getStatus())) {
                log.info("Payment success! Producing Kafka event...");

                //Produce message ...
                paymentProducer.producePaymentEventSuccess(response);

            } else {
                log.warn("Payment not completed: {}", response.getStatus());
            }

            log.info("Payment response! {}", response);

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
    public void updatePaymentStatus(UUID orderId, String status) {

    }

    @Override
    public void sendPaymentSuccessEvent(PaymentResponse paymentResponse) {

    }
}
