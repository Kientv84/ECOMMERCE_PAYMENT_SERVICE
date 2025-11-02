package com.payment.kientv84.services.serviceImpls;

import com.payment.kientv84.dtos.responses.KafkaOrderResponse;
import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.exceptions.EnumError;
import com.payment.kientv84.exceptions.ServiceException;
import com.payment.kientv84.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    @Override
    public void processPayment(KafkaOrderResponse order) {
        log.info("Consume message from order service success! {}", order);

        try {


        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
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
