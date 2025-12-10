package com.payment.kientv84.dtos.requests.search.paymentMethod;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentMethodSearchModel {
    private String q;
    private String status;
    private String code;

    public String hashKey() {
        return ( q + "-" + status + "-" + code);
    }
}
