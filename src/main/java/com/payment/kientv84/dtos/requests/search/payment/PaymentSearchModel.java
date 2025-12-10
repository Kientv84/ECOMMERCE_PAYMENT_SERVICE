package com.payment.kientv84.dtos.requests.search.payment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSearchModel {
    private String q;
    private String status;
    private String transaction_code;

    public String hashKey() {
        return ( q + "-" + status + "-" + transaction_code);
    }
}
