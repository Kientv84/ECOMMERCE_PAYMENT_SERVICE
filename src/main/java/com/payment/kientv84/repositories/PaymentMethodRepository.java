package com.payment.kientv84.repositories;

import com.payment.kientv84.dtos.requests.PaymentMethodRequest;
import com.payment.kientv84.entities.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, UUID> {
    PaymentMethodEntity findPaymentMethodByCode(String code);
}
