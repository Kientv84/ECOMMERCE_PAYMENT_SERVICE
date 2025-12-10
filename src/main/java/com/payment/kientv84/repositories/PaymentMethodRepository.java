package com.payment.kientv84.repositories;

import com.payment.kientv84.dtos.requests.PaymentMethodRequest;
import com.payment.kientv84.entities.PaymentEntity;
import com.payment.kientv84.entities.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethodEntity, UUID>, JpaSpecificationExecutor<PaymentMethodEntity> {
    PaymentMethodEntity findPaymentMethodByCode(String code);

    @Query(value = """
        SELECT * FROM payment_method_entity
        WHERE document_tsv @@ to_tsquery('simple', :q || ':*')
        ORDER BY ts_rank(document_tsv, to_tsquery('simple', :q || ':*')) DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<PaymentMethodEntity> searchPaymentMethodSuggestion(@Param("q") String q, @Param("limit") int limit);
}
