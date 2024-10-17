package io.hhplus.ecommerce.api.application.service;

import io.hhplus.ecommerce.api.application.port.in.OrderUseCase;
import io.hhplus.ecommerce.api.application.port.out.BalanceRepository;
import io.hhplus.ecommerce.api.application.port.out.OrderRepository;
import io.hhplus.ecommerce.api.application.port.out.ProductRepository;
import io.hhplus.ecommerce.api.application.port.out.ProductStockRepository;
import io.hhplus.ecommerce.domain.common.CustomException;
import io.hhplus.ecommerce.domain.common.OrderStatus;
import io.hhplus.ecommerce.domain.common.PaymentStatus;
import io.hhplus.ecommerce.domain.common.StockTransactionType;
import io.hhplus.ecommerce.domain.model.*;
import io.hhplus.ecommerce.dto.request.OrderItemRequest;
import io.hhplus.ecommerce.dto.request.OrderRequest;
import io.hhplus.ecommerce.dto.request.PaymentRequest;
import io.hhplus.ecommerce.dto.response.OrderItemResponse;
import io.hhplus.ecommerce.dto.response.OrderResponse;
import io.hhplus.ecommerce.dto.response.PaymentResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService implements OrderUseCase {

    private final OrderRepository orderRepository;
    private final BalanceRepository balanceRepository;
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;


    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1. 주문 생성 및 저장
        Order order = Order.create(request.getUserId(), OrderStatus.PENDING.getMessage());
        orderRepository.saveOrder(order);

        // 2. 주문 항목 생성 및 재고 검증
        List<OrderItem> orderItems = request.getItems().stream()
                .map(this::createOrderItem)
                .collect(Collectors.toList());

        // 3. 총 금액 계산 후 주문 업데이트
        int totalPrice = calculateTotalPrice(orderItems);
        order.updateTotalPrice(totalPrice);
        orderRepository.updateOrderInfo(order.getId(), OrderStatus.COMPLETED.getMessage());  // 주문 업데이트

        // 4. 주문 응답 생성
        List<OrderItemResponse> orderItemResponses = generateOrderItemResponses(orderItems);

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                totalPrice,
                order.getStatus(),
                orderItemResponses,
                order.getCreateDate(),
                order.getUpdateDate()
        );
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        // 1. 주문 조회
        OrderResponse order = orderRepository.getOrderInfo(request.getOrderId());

        // 2. 주문 상태가 이미 완료된 경우 처리
        if (Objects.equals(order.getStatus(), OrderStatus.COMPLETED.getMessage())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 완료된 주문입니다.");
        }

        // 3. 결제 금액 검증
        int totalPaidAmount = orderRepository.sumPaymentsByOrderId(request.getOrderId());
        int remainingAmount = order.getTotalPrice() - totalPaidAmount;

        if (request.getAmount() > remainingAmount) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "결제 금액이 남은 금액을 초과했습니다.");
        }

        // 4. 잔액 검증 및 차감
        Balance userBalance = balanceRepository.getBalanceByUserId(request.getUserId());
        if (userBalance.getAmount() < request.getAmount()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "잔액이 부족합니다.");
        }
        balanceRepository.deductAmount(request.getUserId(), request.getAmount());

        // 5. 결제 정보 저장
        String transactionId = UUID.randomUUID().toString();
        Payment payment = new Payment(transactionId, request.getOrderId(),
                request.getAmount(), request.getPaymentMethod(), PaymentStatus.PAID.getMessage());
        orderRepository.savePayment(payment);

        // 6. 모든 금액이 결제되면 주문 상태 업데이트
        totalPaidAmount += request.getAmount();
        if (totalPaidAmount == order.getTotalPrice()) {
            orderRepository.updateOrderInfo(order.getId(), OrderStatus.COMPLETED.getMessage());
        }

        // 7. 결제 응답 반환
        return new PaymentResponse(order.getId(), remainingAmount, PaymentStatus.PAID.getMessage(), order.getStatus());
    }

    @Override
    public OrderItem createOrderItem(OrderItemRequest item) {
        int currentStock = productStockRepository.getCurrentStock(item.getProductId());
        Product product = productRepository.getOneProducts(item.getProductId());

        if (currentStock < item.getQuantity()) {
            throw new CustomException(HttpStatus.CONFLICT,
                    "재고 부족: " + product.getName() + "의 남은 재고는 " + currentStock + "개입니다.");
        }

        // 재고 차감
        ProductStock outboundStock = new ProductStock(
                product.getId(), -item.getQuantity(),
                StockTransactionType.OUTBOUND.getMessage(), currentStock - item.getQuantity()
        );

        return new OrderItem(product.getId(), item.getProductId(), item.getQuantity());
    }

    @Override
    public int calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToInt(item -> {
                    Product product = productRepository.getOneProducts(item.getProductId());
                    return product.getPrice() * item.getQuantity();
                })
                .sum();
    }

    @Override
    public List<OrderItemResponse> generateOrderItemResponses(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> {
                    Product product = productRepository.getOneProducts(item.getProductId());
                    return new OrderItemResponse(
                            item.getId(), product.getId(), product.getName(),
                            product.getPrice(), item.getQuantity());
                })
                .collect(Collectors.toList());
    }
}

