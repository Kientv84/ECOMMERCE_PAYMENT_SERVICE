package com.payment.kientv84.services.serviceImpls;

import com.fasterxml.jackson.core.type.TypeReference;
import com.payment.kientv84.commons.Constant;
import com.payment.kientv84.commons.PaymentStatus;
import com.payment.kientv84.dtos.requests.PaymentUpdateRequest;
import com.payment.kientv84.dtos.requests.search.payment.PaymentSearchModel;
import com.payment.kientv84.dtos.requests.search.payment.PaymentSearchOption;
import com.payment.kientv84.dtos.requests.search.payment.PaymentSearchRequest;
import com.payment.kientv84.dtos.responses.PagedResponse;
import com.payment.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.dtos.responses.kafka.KafkaPaymentUpdated;
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
import com.payment.kientv84.services.RedisService;
import com.payment.kientv84.ultis.KafkaObjectError;
import com.payment.kientv84.ultis.PageableUtils;
import com.payment.kientv84.ultis.SpecificationBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final RedisService redisService;


    @Override
    public PagedResponse<PaymentResponse> getAllPayment(PaymentSearchRequest request) {
        log.info("Get all payment api calling...");
        String key = "payments:list:" + request.hashKey();

        try {
            PagedResponse<PaymentResponse> cached = redisService.getValue(key, new TypeReference<PagedResponse<PaymentResponse>>() {
            });

            if (cached != null) {
                log.info("Redis read for key {}", key);
                return cached;
            }

            PaymentSearchOption option = request.getPaymentSearchOption();
            PaymentSearchModel model = request.getPaymentSearchModel();

            List<String> allowedFields = List.of("transactionCode", "createdDate");

            PageRequest pageRequest = PageableUtils.buildPageRequest(
                    option.getPage(),
                    option.getSize(),
                    option.getSort(),
                    allowedFields,
                    "createdDate",
                    Sort.Direction.DESC
            );

            Specification<PaymentEntity> spec = new SpecificationBuilder<PaymentEntity>()
                    .equal("status", model.getStatus())
                    .likeAnyFieldIgnoreCase(model.getQ(), "transactionCode")
                    .build();

            Page<PaymentResponse> result = paymentRepository.findAll(spec, pageRequest)
                    .map(paymentMapper::mapToPaymentResponse);

            PagedResponse<PaymentResponse> response = new PagedResponse<>(
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    result.getContent()
            );

            redisService.setValue(key, response, Constant.SEARCH_CACHE_TTL);

            log.info("Redis MISS, caching search result for key {}", key);

            return response;

        } catch (Exception e) {
            log.error("Error get all orders", e);
            throw new ServiceException(EnumError.PAYMENT_GET_ERROR, "payment.get.error");
        }
    }

    @Override
    public List<PaymentResponse> searchPaymentSuggestion(String q, int limit) {
        List<PaymentEntity> payments = paymentRepository.searchPaymentSuggestion(q, limit);
        return payments.stream().map(pay -> paymentMapper.mapToPaymentResponse(pay)).toList();
    }

    @Override
    public PaymentResponse getPaymentById(UUID id) {
        log.info("Calling get by id api with payment {}", id);

        String key = "payment:"+id;
        try {
            PaymentEntity payment = paymentRepository.findById(id).orElseThrow(()-> new ServiceException(EnumError.PAYMENT_GET_ERROR, "payment.get.error"));

            PaymentResponse response =  paymentMapper.mapToPaymentResponse(payment);
            redisService.setValue(key,response, Constant.CACHE_TTL );

            return response;

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

            // Invalidate cache
            String key = "payment:" + payment.getId();
            redisService.deleteByKey(key);

            redisService.deleteByKeys("payment:" + payment.getId(), "payments:list:*");

            log.info("Cache invalidated for key {}", key);

            return paymentMapper.mapToPaymentResponse(payment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void sendPaymentSuccessEvent(PaymentResponse paymentResponse) {

    }

    @Override
    public void updateStatusFromOrderDelivered(KafkaPaymentUpdated kafkaPaymentUpdated) {
        log.info("Update update Status FromOrder Delivered ...");
        try {
            PaymentEntity payment = paymentRepository.findByOrderId(kafkaPaymentUpdated.getId()).orElseThrow(null);

            if (payment.getStatus() == PaymentStatus.PAID) {
                log.info("Exited status PAID at payment service...");
            }
            payment.setStatus(PaymentStatus.PAID);

            log.info("Update payment status to PAID success...");

            paymentRepository.save(payment);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
