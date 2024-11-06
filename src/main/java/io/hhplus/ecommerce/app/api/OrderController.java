package io.hhplus.ecommerce.app.api;

import io.hhplus.ecommerce.app.application.response.OrderItemResponse;
import io.hhplus.ecommerce.app.application.service.OrderService;
import io.hhplus.ecommerce.app.application.request.OrderRequest;
import io.hhplus.ecommerce.app.application.request.PaymentRequest;
import io.hhplus.ecommerce.app.application.response.OrderResponse;
import io.hhplus.ecommerce.app.application.response.PaymentResponse;
import io.hhplus.ecommerce.app.application.service.ProductService;
import io.hhplus.ecommerce.app.domain.common.CommonApiResponse;
import io.hhplus.ecommerce.app.domain.common.Result;
import io.hhplus.ecommerce.app.domain.model.Order;
import io.hhplus.ecommerce.app.domain.model.OrderItem;
import io.hhplus.ecommerce.app.domain.model.Payment;
import io.hhplus.ecommerce.app.domain.model.Product;
import io.hhplus.ecommerce.app.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order API", description = "주문 관련 API")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    public ResponseEntity<CommonApiResponse<?>> createOrder(@RequestHeader Long userId, @RequestBody OrderRequest request) {

        Order order  = orderService.createOrder(userId, request);
        List<OrderItem> orderItems = orderService.getOrderItem(order.getId());
        List<OrderItemResponse> orderItemResponses = orderItems.stream()
                .map(item -> {
                    Product product = productService.getOneProduct(item.getProductId());
                    if (product == null) {
                        throw new CustomException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다. ID: " + item.getProductId());
                    }

                    return new OrderItemResponse(
                            item.getId(),
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            item.getQuantity()
                    );
                })
                .collect(Collectors.toList());

        OrderResponse orderResponse = new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getStatus(),
                orderItemResponses,
                order.getCreateDate(),
                order.getUpdateDate()
        );


        CommonApiResponse<OrderResponse> response = new CommonApiResponse<>(Result.OK.getStatus(), Result.OK.getMessage(), "상품을 성공적으로 주문했습니다! 결제를 준비해주세요.", orderResponse);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/pay")
    @Operation(summary = "주문 결제", description = "주문을 결제합니다.")
    public ResponseEntity<CommonApiResponse<?>> processPayment(@RequestBody PaymentRequest request) {
        Payment payment = orderService.processPayment(request);
        PaymentResponse paymentResponse = new PaymentResponse(
                payment.getOrderId(),
                payment.getAmount(),
                payment.getStatus()
        );
        CommonApiResponse<PaymentResponse> response = new CommonApiResponse<>(Result.OK.getStatus(), Result.OK.getMessage(), "결제가 성공적으로 완료했습니다! 배송을 준비하겠습니다.", paymentResponse);

        return ResponseEntity.ok(response);

    }

}

