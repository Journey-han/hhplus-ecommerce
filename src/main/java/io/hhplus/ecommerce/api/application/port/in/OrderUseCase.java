package io.hhplus.ecommerce.api.application.port.in;

import io.hhplus.ecommerce.domain.model.OrderItem;
import io.hhplus.ecommerce.dto.request.OrderItemRequest;
import io.hhplus.ecommerce.dto.request.OrderRequest;
import io.hhplus.ecommerce.dto.request.PaymentRequest;
import io.hhplus.ecommerce.dto.response.OrderItemResponse;
import io.hhplus.ecommerce.dto.response.OrderResponse;
import io.hhplus.ecommerce.dto.response.PaymentResponse;

import java.util.List;

public interface OrderUseCase {

    OrderResponse createOrder(OrderRequest request);
    PaymentResponse processPayment(PaymentRequest paymentRequest);
    OrderItem createOrderItem(OrderItemRequest item);
    int calculateTotalPrice(List<OrderItem> orderItems);
    List<OrderItemResponse> generateOrderItemResponses(List<OrderItem> orderItems);

}
