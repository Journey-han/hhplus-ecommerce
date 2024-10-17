package io.hhplus.ecommerce.api.application.port.out;

import io.hhplus.ecommerce.domain.model.Order;
import io.hhplus.ecommerce.domain.model.Payment;
import io.hhplus.ecommerce.dto.response.OrderResponse;
import jakarta.transaction.Transactional;

public interface OrderRepository {
    void saveOrder(Order order);
    OrderResponse getOrderInfo(Long orderId);
    void updateOrderInfo(Long orderId, String status);
    int sumPaymentsByOrderId(Long orderId);
    void savePayment(Payment payment);
}
