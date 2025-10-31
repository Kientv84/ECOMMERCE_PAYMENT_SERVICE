package com.payment.kientv84.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum EnumError {

    //----------- PAYMENT ------------
    ORDER_DATA_EXISTED("ORDER-DTE", "Data exit", HttpStatus.CONFLICT),

    ORDER_GET_ERROR("ORDER-GET-ERROR", "Have error in process get order", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("ACC-S-999", "Unexpected internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    ORDER_ERR_NOT_FOUND("ORDER-CATE_NF", "Not found sub order with id", HttpStatus.BAD_REQUEST),
    ORDER_ERR_DEL_EM("ORDER-CATE-GA", "List ids to delete is empty", HttpStatus.BAD_REQUEST),
    //----------- EXTERNAL SERVICES ------------
    PRODUCT_NOT_FOUND("PRODUCT-404", "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_SERVICE_UNAVAILABLE("PRODUCT-503", "Product service unavailable", HttpStatus.SERVICE_UNAVAILABLE);

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

