package com.payment.kientv84.dtos.requests.search.paymentMethod;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentMethodSearchRequest {
    private PaymentMethodSearchModel paymentMethodSearchModel = new PaymentMethodSearchModel();
    private PaymentMethodSearchOption paymentMethodSearchOption = new PaymentMethodSearchOption();

    public String hashKey() {
        return "option:" + paymentMethodSearchOption.hashKey() + "|filter:" + paymentMethodSearchModel.hashKey();
    }
}
