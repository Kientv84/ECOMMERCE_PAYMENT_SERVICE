package com.payment.kientv84.entities;

import com.payment.kientv84.commons.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "payment_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class PaymentEntity{

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    // Liên kết với orderId từ Order Service
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "order_code", nullable = false)
    private String orderCode;

    // Tổng tiền thanh toán (copy từ order để tiện tracking)
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    // Phương thức thanh toán: COD, MOMO, VNPAY, PAYPAL...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    private PaymentMethodEntity paymentMethod;

    // Mã giao dịch của bên thứ 3 (ví dụ transactionId Momo)
    @Column(name = "transaction_code", unique = true)
    private String transactionCode;

    // Trạng thái thanh toán
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    // Thời điểm thanh toán
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // Ghi chú lỗi hoặc mô tả thêm
    @Column(name = "note")
    private String note;

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

    @PrePersist
    public void prePersist() {
        if (status == null) status = PaymentStatus.PENDING;
    }
}
