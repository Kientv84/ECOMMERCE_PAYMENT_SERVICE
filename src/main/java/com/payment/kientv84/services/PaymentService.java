package com.payment.kientv84.services;

import com.payment.kientv84.dtos.requests.PaymentUpdateRequest;
import com.payment.kientv84.dtos.responses.kafka.KafkaOrderResponse;
import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.dtos.responses.kafka.KafkaPaymentUpdated;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    /**
     * - Api system
     */

    List<PaymentResponse> getAllPayment();

    PaymentResponse getPaymentById(UUID id);

    /**
     * Giao tiếp với Kafka
     */

    /**
     * Xử lý khi nhận được order mới từ Kafka
     * - Lưu PaymentEntity
     * - Gọi gateway kiểm tra (nếu có)
     * - Cập nhật trạng thái payment
     * - Gửi Kafka message nếu thành công
     */
    void processPayment(KafkaOrderResponse order);

    /**
     * Lấy thông tin thanh toán theo OrderId
     */
    PaymentResponse getPaymentByOrderId(UUID orderId);

    /**
     * Cập nhật trạng thái thanh toán (nếu nhận tín hiệu từ gateway hoặc service khác)
     */
    PaymentResponse updatePaymentStatus(UUID orderId, PaymentUpdateRequest updateRequest);

    /**
     * Gửi Kafka event "payment-success" cho các service khác
     */
    void sendPaymentSuccessEvent(PaymentResponse paymentResponse);

    void updateStatusFromOrderDelivered(KafkaPaymentUpdated kafkaPaymentUpdated);
}
