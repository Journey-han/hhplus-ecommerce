package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.infrastructure.persistence.ProductStockRepository;
import io.hhplus.ecommerce.app.domain.model.*;
import io.hhplus.ecommerce.app.exception.CustomException;
import io.hhplus.ecommerce.app.domain.common.OrderStatus;
import io.hhplus.ecommerce.app.domain.common.PaymentStatus;
import io.hhplus.ecommerce.app.domain.common.StockTransactionType;
import io.hhplus.ecommerce.app.application.request.OrderItemRequest;
import io.hhplus.ecommerce.app.application.request.OrderRequest;
import io.hhplus.ecommerce.app.application.request.PaymentRequest;
import io.hhplus.ecommerce.app.application.response.OrderItemResponse;
import io.hhplus.ecommerce.app.application.response.OrderResponse;
import io.hhplus.ecommerce.app.application.response.PaymentResponse;

import io.hhplus.ecommerce.app.infrastructure.persistence.BalanceRepositoryImpl;
import io.hhplus.ecommerce.app.infrastructure.persistence.OrderRepositoryImpl;
import io.hhplus.ecommerce.app.infrastructure.persistence.ProductRepositoryImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderRepositoryImpl orderRepository;
    private final BalanceRepositoryImpl balanceRepository;
    private final ProductRepositoryImpl productRepository;
    private final ProductStockRepository productStockRepository;


    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.debug("request : {}", request);

        // 1. 주문 생성 및 저장
        Order order = Order.create(request.getUserId(), OrderStatus.PENDING.getMessage());
        orderRepository.saveOrder(order);

        // 2. 주문 항목 생성 및 재고 예약
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> reserveInventory(item, order.getId())) // 재고 예약 수행
                .collect(Collectors.toList());

        // 3. 총 금액 계산 후 주문 업데이트
        int totalPrice = calculateTotalPrice(orderItems);
        order.updateTotalPrice(totalPrice);
        orderRepository.updateOrderInfo(order.getId(), OrderStatus.COMPLETED.getMessage());  // 주문 업데이트

        // 4. 주문 응답 생성
        List<OrderItemResponse> orderItemResponses = generateOrderItemResponses(orderItems);

        log.debug("orderItemResponse : {}", orderItemResponses);

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

    // 재고 예약 수행 메서드
    private OrderItem reserveInventory(OrderItemRequest itemRequest, Long orderId) {
        log.debug("itemRequest : {}", itemRequest);
        log.debug("orderId : {}", orderId);

        // 1. 상품 정보 조회
        Product product = productRepository.getOneProducts(itemRequest.getProductId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

        // 2. 상품 재고 정보 조회
        ProductStock productStock = productStockRepository.findByProductId(itemRequest.getProductId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "상품의 재고 정보를 찾을 수 없습니다."));

        // 3. 재고 수량 검증 및 예약
        if (productStock.getStock() < itemRequest.getQuantity()) {
            throw new CustomException(HttpStatus.CONFLICT,
                    "재고 부족: 상품 '" + product.getName() + "'의 남은 재고는 "
                            + productStock.getStock() + "개입니다.");
        }
        productStock.reserveStock(itemRequest.getQuantity());
        productStockRepository.save(productStock);

        // 4. 주문 항목 생성
        return new OrderItem(product.getId(), orderId, itemRequest.getQuantity());
    }

    // 결제 실패 시 재고 해제
    @Transactional
    public void releaseInventory(Long orderId) {
        log.debug("orderId : {}", orderId);

        // 1. 주문 항목 조회
        List<OrderItem> orderItems = orderRepository.findByOrderId(orderId);

        // 2. 재고 복원 처리
        for (OrderItem item : orderItems) {
            ProductStock productStock = productStockRepository.findByProductId(item.getProductId())
                    .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                            "상품 재고 정보를 찾을 수 없습니다."));

            // 3. 재고 복원: 기존 재고에 수량을 더함
            productStock.releaseStock(item.getQuantity());

            // 4. 재고 업데이트
            productStockRepository.save(productStock);
        }
    }

    public OrderItem createOrderItem(OrderItemRequest item) {

        int currentStock = productStockRepository.getCurrentStock(item.getProductId());

        Product product = productRepository.getOneProducts(item.getProductId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                        "상품을 찾을 수 없습니다: " + item.getProductId()));

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

    public int calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .mapToInt(item -> {
                    Product product = productRepository.getOneProducts(item.getProductId())
                            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                    "상품을 찾을 수 없습니다: " + item.getProductId()));

                    return product.getPrice() * item.getQuantity();
                })
                .sum();
    }

    public List<OrderItemResponse> generateOrderItemResponses(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(item -> {
                    Product product = productRepository.getOneProducts(item.getProductId())
                            .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,
                                    "상품을 찾을 수 없습니다: " + item.getProductId()));

                    return new OrderItemResponse(
                            item.getId(), product.getId(), product.getName(),
                            product.getPrice(), item.getQuantity());
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.debug("request : {}", request);

        // 1. 주문 조회
        Order order = orderRepository.getOrderInfo(request.getOrderId());

        // 2. 주문 상태가 이미 완료된 경우 처리
        if (Objects.equals(order.getStatus(), OrderStatus.CANCELLED.getMessage())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "취소된 주문입니다.");
        }

        try {
            // 3. 결제 금액 검증
            int totalPaidAmount = orderRepository.sumPaymentsByOrderId(request.getOrderId());
            int remainingAmount = order.getTotalPrice() - totalPaidAmount;
            log.debug("총 결제된 금액: {}, 남은 결제 금액: {}", totalPaidAmount, remainingAmount);

            if (request.getAmount() > remainingAmount) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "결제 금액이 남은 금액을 초과했습니다.");
            }

            // 4. 잔액 검증 및 차감
            Balance userBalance = balanceRepository.getBalanceByUserId(request.getUserId());
            if (userBalance.getAmount() < request.getAmount()) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "잔액이 부족합니다.");
            }
           userBalance.minusAmount(request.getAmount());

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

        } catch (Exception e) {
            // 결제 실패 시 재고 해제
            releaseInventory(order.getId());
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 실패: " + e.getMessage());

        }

    }
}

