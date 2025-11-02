package com.payment.kientv84.commons;

public enum PaymentMethodStatus {
    ACTIVE,      // Phương thức thanh toán đang hoạt động, có thể sử dụng
    INACTIVE,    // Phương thức thanh toán bị vô hiệu hóa, không thể sử dụng
    MAINTENANCE  // Phương thức đang được bảo trì (tạm thời không khả dụng)
}