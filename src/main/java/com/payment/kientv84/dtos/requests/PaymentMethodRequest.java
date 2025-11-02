package com.payment.kientv84.dtos.requests;

import com.payment.kientv84.commons.PaymentMethodStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
public class PaymentMethodRequest {
    @NotNull(message = "{payment.method.code.notnull}")
    private String code;
    @NotNull(message = "{payment.method.name.notnull}")
    private String name;
    @NotNull(message = "{payment.method.description.notnull}")
    private String description;
//    @NotNull(message = "{payment.method.status.notnull}")
//    private PaymentMethodStatus status;
}
