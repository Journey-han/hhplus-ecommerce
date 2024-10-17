package io.hhplus.ecommerce.dto.request;

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

    @Schema(description = "상품 가격", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
    private int price;

    @Schema(description = "날짜", example = "2024-10-17", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createDate;


    public OrderItemRequest(long productId, int quantity, int price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.createDate = LocalDateTime.now();
    }
}
