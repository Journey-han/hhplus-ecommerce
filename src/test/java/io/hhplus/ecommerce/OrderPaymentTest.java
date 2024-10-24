package io.hhplus.ecommerce;

import io.hhplus.ecommerce.api.application.port.out.BalanceRepository;
import io.hhplus.ecommerce.api.application.port.out.ProductStockRepository;
import io.hhplus.ecommerce.api.application.service.OrderService;
import io.hhplus.ecommerce.domain.common.CustomException;
import io.hhplus.ecommerce.domain.common.OrderStatus;
import io.hhplus.ecommerce.domain.common.StockTransactionType;
import io.hhplus.ecommerce.domain.model.Balance;
import io.hhplus.ecommerce.domain.model.ProductStock;
import io.hhplus.ecommerce.dto.request.OrderItemRequest;
import io.hhplus.ecommerce.dto.request.OrderRequest;
import io.hhplus.ecommerce.dto.request.PaymentRequest;
import io.hhplus.ecommerce.dto.response.OrderResponse;
import io.hhplus.ecommerce.dto.response.PaymentResponse;
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
        balanceRepository.save(new Balance(1L, 1L, 100000));
        productStockRepository.save(new ProductStock(1L, 100, StockTransactionType.RESTOCKED.getMessage(), 100));

        // 2. 주문 요청 생성
        OrderRequest request = new OrderRequest(1L, List.of(
                new OrderItemRequest(1L, 3, 300)  // 상품 1번을 3개 주문
        ));

        // 3. 주문 생성 및 검증
        OrderResponse orderResponse = orderService.createOrder(request);
        assertThat(orderResponse).isNotNull();
        assertThat(orderResponse.getTotalPrice()).isEqualTo(300);  // 상품 가격이 100원일 경우
        assertThat(orderResponse.getStatus()).isEqualTo(OrderStatus.COMPLETED.getMessage());

        // 4. 결제 요청 생성
        PaymentRequest paymentRequest = new PaymentRequest(orderResponse.getId(), 1L, 300, "balance");

        // 5. 결제 수행 및 검증
        PaymentResponse paymentResponse = orderService.processPayment(paymentRequest);
        assertThat(paymentResponse).isNotNull();
        assertThat(paymentResponse.getStatus()).isEqualTo(OrderStatus.COMPLETED.getMessage());
    }

    @Test
    @DisplayName("잔액 부족 결제 실패 테스트")
    public void lessBalanceFailedPayment() {
        // 1. 사용자 잔액 설정 (잔액 부족)
        balanceRepository.save(new Balance(1L, 1L, 100));

        // 2. 주문 요청 생성
        OrderRequest request = new OrderRequest(1L, List.of(
                new OrderItemRequest(1L, 3, 1000)  // 상품 1번을 3개 주문
        ));
        OrderResponse orderResponse = orderService.createOrder(request);

        // 3. 결제 요청 생성 (부족한 잔액으로 결제)
        PaymentRequest paymentRequest = new PaymentRequest(orderResponse.getId(), 1L, 300, "balance");

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
        productStockRepository.save(new ProductStock(1L, 2, StockTransactionType.RESTOCKED.getMessage(), 2));  // 재고 2개

        // 2. 주문 요청 생성 (3개 주문 시도)
        OrderRequest request = new OrderRequest(1L, List.of(
                new OrderItemRequest(1L, 3, 1000)
        ));

        // 3. 주문 실패 검증
        CustomException exception = assertThrows(CustomException.class,
                () -> orderService.createOrder(request));

        assertThat(exception.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(exception.getMessage()).contains("재고 부족");
    }
}
