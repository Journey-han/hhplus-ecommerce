package io.hhplus.ecommerce.app.application.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "결제 응답 객체")
public class PaymentResponse {

    @Schema(description = "주문 ID", example = "11", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long orderId;

    @Schema(description = "결제 금액", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
    private int amount;

    @Schema(description = "결제 방법", example = "BALANCE", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paymentMethod;

    @Schema(description = "결제 상태", example = "PAID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(description = "결제 생성 시간", example = "2024-10-18T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createDate;

    public PaymentResponse(Long orderId, int amount, String paymentMethod, String status) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }
}