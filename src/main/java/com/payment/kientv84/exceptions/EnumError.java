package com.payment.kientv84.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EnumError {

    //----------- PAYMENT METHOD------------
    PAYMENT_METHOD_DATA_EXISTED("PAYMENT-METHOD-DTE", "Data exit", HttpStatus.CONFLICT),

    PAYMENT_METHOD_GET_ERROR("PAYMENT-METHOD-GET-ERROR", "Have error in process get payment method", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_ERR_NOT_FOUND("PAYMENT-METHOD-CATE_NF", "Not found sub payment method with id", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_ERR_DEL_EM("PAYMENT-METHOD-CATE-GA", "List ids to delete is empty", HttpStatus.BAD_REQUEST),
    PAYMENT_METHOD_NOT_FOUND("PAYMENT-METHOD-404", "payment method not found", HttpStatus.NOT_FOUND),
    PAYMENT_METHOD_SERVICE_UNAVAILABLE("PAYMENT-METHOD-503", "payment method service unavailable", HttpStatus.SERVICE_UNAVAILABLE),

    //----------- PAYMENT------------
    PAYMENT_DATA_EXISTED("PAYMENT-DTE", "Data exit", HttpStatus.CONFLICT),
    PAYMENT_GET_ERROR("PAYMENT-GET-ERROR", "Have error in process get payment", HttpStatus.BAD_REQUEST),
    PAYMENT_ERR_NOT_FOUND("PAYMENT-CATE_NF", "Not found sub payment with id", HttpStatus.BAD_REQUEST),
    PAYMENT_ERR_DEL_EM("PAYMENT-CATE-GA", "List ids to delete is empty", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_FOUND("PAYMENT-404", "payment not found", HttpStatus.NOT_FOUND),
    PAYMENT_SERVICE_UNAVAILABLE("PAYMENT-503", "payment service unavailable", HttpStatus.SERVICE_UNAVAILABLE),

    INVALID_PAYMENT_METHOD("INVALID-METHOD", "Invalid payment method", HttpStatus.NOT_FOUND),

    PAYMENT_PROCESS_NOTNULL("PAYMENT-PROCESS-NULL", "Payment argument be null!", HttpStatus.BAD_REQUEST),
    //----------- EXTERNAL SERVICES ------------
    INTERNAL_ERROR("PAYMENT-S-999", "Unexpected internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;


    public static EnumError fromCode(String code) {
        for (EnumError e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Unknown DispatchError code: " + code);
    }
}

