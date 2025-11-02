package com.payment.kientv84.dtos.responses;

import com.payment.kientv84.commons.PaymentMethodStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class PaymentMethodResponse {
    private UUID id;
    private String code; // "COD", "MOMO", "VNPAY", ...
    private String name; // Ví dụ: "Thanh toán khi nhận hàng"
    private String status;
    private String description;
//    private Date createdDate;
//    private Date updatedDate;
//    private String createdBy;
//    private String updatedBy;
}
