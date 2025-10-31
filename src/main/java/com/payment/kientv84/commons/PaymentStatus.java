package com.payment.kientv84.commons;

public enum PaymentStatus {
    PENDING,       // Đang chờ thanh toán (ví dụ COD chưa giao)
    PROCESSING,    // Đang xác thực bên thứ 3 (Momo, VNPay...)
    SUCCESS,       // Thanh toán thành công
    FAILED,        // Thanh toán thất bại
    CANCELLED      // Đã huỷ thanh toán
}
