package com.payment.kientv84.mocks.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomoPaymentRequest {
    private String orderCode;      // Mã đơn hàng, ví dụ: ORD-1234
    private BigDecimal amount;     // Số tiền thanh toán
    private String transactionId;  // Mã giao dịch giả lập từ MoMo
    private String paymentMethod;  // "MOMO" hoặc "QR_MOMO" (nếu cần)
    private String customerPhone;  // Số điện thoại người thanh toán (tùy chọn)
}
