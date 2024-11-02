package io.hhplus.ecommerce.app.api;

import io.hhplus.ecommerce.app.application.response.BalanceResponse;
import io.hhplus.ecommerce.app.application.service.OrderService;
import io.hhplus.ecommerce.app.application.request.OrderRequest;
import io.hhplus.ecommerce.app.application.request.PaymentRequest;
import io.hhplus.ecommerce.app.application.response.OrderResponse;
import io.hhplus.ecommerce.app.application.response.PaymentResponse;
import io.hhplus.ecommerce.app.domain.common.CommonApiResponse;
import io.hhplus.ecommerce.app.domain.common.Result;
import io.hhplus.ecommerce.app.domain.model.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order API", description = "주문 관련 API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    public ResponseEntity<CommonApiResponse<?>> createOrder(@RequestHeader Long userId, @RequestBody OrderRequest request) {

        OrderResponse orderResponse = orderService.createOrder(userId, request);
        CommonApiResponse<OrderResponse> response = new CommonApiResponse<>(Result.OK.getStatus(), Result.OK.getMessage(), "상품을 성공적으로 주문했습니다! 결제를 준비해주세요.", orderResponse);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/pay")
    @Operation(summary = "주문 결제", description = "주문을 결제합니다.")
    public ResponseEntity<CommonApiResponse<?>> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse paymentResponse = orderService.processPayment(request);
        CommonApiResponse<PaymentResponse> response = new CommonApiResponse<>(Result.OK.getStatus(), Result.OK.getMessage(), "결제가 성공적으로 완료했습니다! 배송을 준비하겠습니다.", paymentResponse);

        return ResponseEntity.ok(response);

    }

}

