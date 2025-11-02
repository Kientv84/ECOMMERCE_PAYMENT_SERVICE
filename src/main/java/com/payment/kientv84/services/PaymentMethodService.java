package com.payment.kientv84.services;

import com.payment.kientv84.dtos.requests.PaymentMethodRequest;
import com.payment.kientv84.dtos.requests.PaymentMethodUpdateRequest;
import com.payment.kientv84.dtos.responses.PaymentMethodResponse;

import java.util.List;
import java.util.UUID;

public interface PaymentMethodService {
    List<PaymentMethodResponse> getAllPaymentMethod();

    PaymentMethodResponse createPaymentMethod(PaymentMethodRequest request);

    PaymentMethodResponse getPaymentMethodById(UUID id);

    PaymentMethodResponse updatePaymentMethodById(UUID id, PaymentMethodUpdateRequest updateRequest);

    String deletePaymentMethod(List<UUID> ids);
}
