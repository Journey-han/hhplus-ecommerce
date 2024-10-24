package io.hhplus.ecommerce.app.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "결제 요청")
public class PaymentRequest {

    @Schema(description = "유저 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
    @Schema(description = "주문 ID", example = "11", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;
    @Schema(description = "결제 금액", example = "2000", requiredMode = Schema.RequiredMode.REQUIRED)
    private int amount;
    @Schema(description = "결제 방법", example = "BALANCE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paymentMethod;
    @Schema(description = "결제 날짜", example = "2024-10-17", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createDate;
    @Schema(description = "주문 상품 ID", example = "2024-10-17", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateDate;


    public PaymentRequest(Long orderId, Long userId, int amount, String paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }


}
