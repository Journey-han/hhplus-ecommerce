package io.hhplus.ecommerce;

import io.hhplus.ecommerce.app.domain.model.Order;
import io.hhplus.ecommerce.app.domain.model.Payment;
import io.hhplus.ecommerce.app.infrastructure.persistence.ProductStockRepository;
import io.hhplus.ecommerce.app.application.service.OrderService;
import io.hhplus.ecommerce.app.exception.CustomException;
import io.hhplus.ecommerce.app.domain.common.OrderStatus;
import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.domain.model.ProductStock;
import io.hhplus.ecommerce.app.application.request.OrderItemRequest;
import io.hhplus.ecommerce.app.application.request.OrderRequest;
import io.hhplus.ecommerce.app.application.request.PaymentRequest;
import io.hhplus.ecommerce.app.infrastructure.persistence.BalanceRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = HhplusEcommerceApplication.class)
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class OrderPaymentTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Test
    @DisplayName("주문 결제 성공 테스트")
    public void successOrderPayment() {
        // 1. 사용자 잔액과 상품 재고 초기화
        balanceRepository.save(new Balance(1001L, 100000));
        productStockRepository.save(new ProductStock(1L, 100));

        // 2. 주문 요청 생성
        OrderRequest request = new OrderRequest(List.of(
                new OrderItemRequest(1L, 3)  // 상품 1번을 3개 주문
        ));

        // 3. 주문 생성 및 검증
        Order order = orderService.createOrder(10001L,request);
        assertThat(order).isNotNull();
        assertThat(order.getTotalPrice()).isEqualTo(300);  // 상품 가격이 100원일 경우
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED.getMessage());

        // 4. 결제 요청 생성
        PaymentRequest paymentRequest = new PaymentRequest(order.getId(), 1L, 300);

        // 5. 결제 수행 및 검증
        Payment payment = orderService.processPayment(paymentRequest);
        assertThat(payment).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(OrderStatus.COMPLETED.getMessage());
    }

    @Test
    @DisplayName("잔액 부족 결제 실패 테스트")
    public void lessBalanceFailedPayment() {
        // 1. 사용자 잔액 설정 (잔액 부족)
        balanceRepository.save(new Balance(10001L, 100));

        // 2. 주문 요청 생성
        OrderRequest request = new OrderRequest(List.of(
                new OrderItemRequest(1L, 3)  // 상품 1번을 3개 주문
        ));

        Order order = orderService.createOrder(1001L, request);

        // 3. 결제 요청 생성 (부족한 잔액으로 결제)
        PaymentRequest paymentRequest = new PaymentRequest(order.getId(), 1L, 300);

        // 4. 결제 실패 검증
        CustomException exception = assertThrows(CustomException.class,
                () -> orderService.processPayment(paymentRequest));

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getMessage()).isEqualTo("잔액이 부족합니다.");
    }

    @Test
    @DisplayName("재고 부족 주문 실패 테스트")
    public void lessStockFailedOrder() {
        // 1. 상품 재고 설정 (재고 부족)
        productStockRepository.save(new ProductStock(1L,  2));  // 재고 2개

        // 2. 주문 요청 생성 (3개 주문 시도)
        OrderRequest request = new OrderRequest(List.of(
                new OrderItemRequest(1L, 3)
        ));

        // 3. 주문 실패 검증
        CustomException exception = assertThrows(CustomException.class,
                () -> orderService.createOrder(1001L, request));

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getMessage()).contains("재고 부족");
    }

    @Test
    @DisplayName("결제 금액 초과 시 결제 실패 테스트")
    public void paymentFailsDueToExcessAmount() {
        // 1. 사용자 잔액 설정
        balanceRepository.save(new Balance(1L, 500)); // 잔액: 500원

        // 2. 주문 요청 생성
        OrderRequest orderRequest = new OrderRequest(List.of(
                new OrderItemRequest(1L, 2)
        ));

        Order order = orderService.createOrder(1001L, orderRequest);

        // 3. 결제 요청 (잔액 부족으로 실패)
        PaymentRequest paymentRequest = new PaymentRequest(order.getId(), 1L, 1000);
        assertThrows(CustomException.class, () -> orderService.processPayment(paymentRequest));
    }

    @Test
    @DisplayName("결제 실패 시 재고 복구 테스트")
    public void paymentFailureRestoresInventoryTest() {
        // 재고 초기화
        productStockRepository.save(new ProductStock(1L, 100));

        // 주문 생성
        OrderRequest orderRequest = new OrderRequest(List.of(
                new OrderItemRequest(1L, 3)
        ));

        Order order = orderService.createOrder(1001L, orderRequest);

        // 결제 실패 처리
        assertThrows(CustomException.class, () -> orderService.processPayment(
                new PaymentRequest(order.getId(), 1L, 50000)));

        // 재고 복구 검증
        ProductStock productStock = productStockRepository.findByProductId(1L).orElseThrow();
        assertThat(productStock.getStock()).isEqualTo(100);  // 원래 재고로 복구됨
    }
}
