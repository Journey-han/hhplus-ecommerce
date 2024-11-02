package io.hhplus.ecommerce.app.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 항목")
public class OrderItemRequest {

    @Schema(description = "상품 ID", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;

    @Schema(description = "주문 수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private int quantity;


    public OrderItemRequest(long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
