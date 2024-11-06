package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.domain.common.BondStatus;
import io.hhplus.ecommerce.app.domain.common.UserStatus;
import io.hhplus.ecommerce.app.infrastructure.persistence.*;
import io.hhplus.ecommerce.app.domain.model.*;
import io.hhplus.ecommerce.app.exception.CustomException;
import io.hhplus.ecommerce.app.domain.common.OrderStatus;
import io.hhplus.ecommerce.app.domain.common.PaymentStatus;
import io.hhplus.ecommerce.app.application.request.OrderItemRequest;
import io.hhplus.ecommerce.app.application.request.OrderRequest;
import io.hhplus.ecommerce.app.application.request.PaymentRequest;
import io.hhplus.ecommerce.app.application.response.OrderItemResponse;
import io.hhplus.ecommerce.app.application.response.OrderResponse;
import io.hhplus.ecommerce.app.application.response.PaymentResponse;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BondRepository bondRepository;
    private final BalanceRepository balanceRepository;
    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final ProductStockService productStockService;
    private final BalanceService balanceService;

    public List<OrderItem> getOrderItem(Long orderId) {
        return orderRepository.getOrderItem(orderId);
    }


    /**
     * 2.1 주문생성
     * @param request
     * @return
     */
    @Transactional
    public Order createOrder(Long userId, OrderRequest request) {
        log.debug("request : {}", request);

        User user = userRepository.findByUserId(userId);
        if (user == null || Objects.equals(user.getStatus(), UserStatus.DEACTIVATE.getMessage())) {
            log.info("User not found for userId={}", userId);
            throw new CustomException(HttpStatus.NOT_FOUND, "고객 정보를 찾을 수 없습니다. 요청한 userId=" + userId);
        } else {
            // 1. 주문 생성 및 저장
            Order order = Order.create(userId, OrderStatus.PENDING.getMessage());
            orderRepository.saveOrder(order);

            // 2. 주문 항목 생성 및 재고 예약
            List<OrderItem> orderItems = request.getItems().stream()
                    .map(item -> reserveInventory(item, order.getId())) // 재고 예약 수행
                    .collect(Collectors.toList());
            orderItemRepository.saveAll(orderItems);

            // 3. 총 금액 계산
            int totalPrice = calculateTotalPrice(orderItems);
            order.updateTotalPrice(totalPrice);
            Bond bond = new Bond(order.getId(), totalPrice, BondStatus.ISSUED.getMessage());

            // 4. 해당 주문에 대한 채권 생성
            bondRepository.save(bond);
            orderRepository.saveOrder(order);

            //5. 주문 응답 생성
            List<OrderItemResponse> orderItemResponses = generateOrderItemResponses(orderItems);

           log.debug("orderItemResponse : {}", orderItemResponses);

            return order;
        }

    }

    /**
     * 2.1.2 주문 아이템 생성
     * @param orderItems
     * @return
     */
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


    /**
     * 2.1.2 상품 재고 차감 예약 수행 (주문 항목 생성시)
     * @param itemRequest
     * @param orderId
     * @return
     */
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


    /**
     * 2.1.3 재고 차감 예약 해제 (결제 실패시)
     * @param orderId
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void releaseInventory(Long orderId) {
        log.debug("orderId : {}", orderId);

        // 1. 주문 항목 조회
        List<OrderItem> orderItems = orderRepository.getOrderItem(orderId);

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
        orderRepository.updateOrderInfo(orderId, OrderStatus.CANCELLED.getMessage());
    }


    /**
     * 주문항목 생성 및 재고 차감
     * @param item
     * @return
     */
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
                product.getId(), currentStock - item.getQuantity()
        );

        return new OrderItem(product.getId(), item.getProductId(), item.getQuantity());
    }


    /**
     * 2.1.4 총 주문 금액 계산
     * @param orderItems
     * @return
     */
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



    /**
     * 3.1 결제 수행
     * @param request
     * @return
     */
    @Transactional
    public Payment processPayment(PaymentRequest request) {
        log.debug("request : {}", request);

        // 1. 주문에 대한 채권 조회
        Bond bond = bondRepository.findByOrderId(request.getOrderId());

        // 2. 이미 정산된 채권인지 확인
        if (bond.getStatus().equals(BondStatus.SETTLED.getMessage())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미 결제가 완료된 주문입니다.");
        }

        // 3. 트랜잭션 ID 생성 및 결제 수행SELECT * FROM ORDERS
        Payment payment = new Payment(request.getOrderId(), request.getAmount(), PaymentStatus.WAITING.getMessage());
        orderRepository.savePayment(payment);


        try {
            // 4. 결제 금액 검증
            int totalPaidAmount = orderRepository.sumPaymentsByOrderId(request.getOrderId());
            int remainingAmount = bond.getAmount() - totalPaidAmount;
            log.debug("총 결제된 금액: {}, 남은 결제 금액: {}", totalPaidAmount, remainingAmount);

            if (request.getAmount() < remainingAmount) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "결제 금액이 남은 금액을 초과했습니다.");
            }

            // 5. 잔액 검증 및 차감
           Balance decutBalance = balanceService.deductBalance(request.getUserId(),request.getAmount());

           // 6. 모든 금액이 결제되면 주문 상태 업데이트
           if (totalPaidAmount == request.getAmount()) {
               orderRepository.updateOrderInfo(bond.getOrderId(), OrderStatus.COMPLETED.getMessage());
               orderRepository.updatePaymentInfo(payment.getTxKey(), PaymentStatus.PAID.getMessage());
               bond.settle(bond.getBondKey());}

           // 7. 결제 응답 반환
           return new Payment(bond.getOrderId(), bond.getAmount(), PaymentStatus.PAID.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("releaseInventory start");
            // 결제 실패 시 재고 해제
            releaseInventory(bond.getOrderId());
            System.out.println("releaseInventory end");
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 실패: " + e.getMessage());

        }

    }
}

