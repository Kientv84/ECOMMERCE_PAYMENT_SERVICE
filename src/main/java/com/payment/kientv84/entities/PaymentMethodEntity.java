package com.payment.kientv84.entities;

import com.payment.kientv84.commons.PaymentMethodStatus;
import com.payment.kientv84.commons.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "payment_method_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PaymentMethodEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code; // "COD", "MOMO", "VNPAY", ...

    @Column(name = "name", nullable = false, length = 100)
    private String name; // Ví dụ: "Thanh toán khi nhận hàng"

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private PaymentMethodStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private PaymentEntity paymentId;
    // ====== Metadata ======
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name ="create_date")
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @Column(name ="update_date")
    private Date updatedDate;

    @CreatedBy
    @Column(name ="created_by")
    private String createdBy;

    @LastModifiedBy
    @Column(name ="updated_by")
    private String updatedBy;
}