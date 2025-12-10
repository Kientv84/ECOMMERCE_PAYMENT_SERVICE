package com.payment.kientv84.dtos.requests.search.payment;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSearchRequest {
    private PaymentSearchOption paymentSearchOption = new PaymentSearchOption();
    private PaymentSearchModel paymentSearchModel = new PaymentSearchModel();

    public String hashKey() {
        return "option:" + paymentSearchOption.hashKey() + "|filter:" + paymentSearchModel.hashKey();
    }
}
