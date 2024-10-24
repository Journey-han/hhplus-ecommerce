package io.hhplus.ecommerce.app.api;

import io.hhplus.ecommerce.app.application.service.OrderService;
import io.hhplus.ecommerce.app.application.request.OrderRequest;
import io.hhplus.ecommerce.app.application.request.PaymentRequest;
import io.hhplus.ecommerce.app.application.response.OrderResponse;
import io.hhplus.ecommerce.app.application.response.PaymentResponse;
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
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {

        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PostMapping("/pay")
    @Operation(summary = "주문 결제", description = "주문을 결제합니다.")
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = orderService.processPayment(request);
        return  ResponseEntity.status(HttpStatus.OK).body(response);

    }

}

