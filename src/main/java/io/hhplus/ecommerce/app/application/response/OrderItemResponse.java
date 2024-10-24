package io.hhplus.ecommerce.app.application.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 상품 항목")
public class OrderItemResponse {
    @Schema(description = "주문 상품 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    @Schema(description = "상품 ID", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long productId;
    @Schema(description = "상품 이름", example = "컴퓨터", requiredMode = Schema.RequiredMode.REQUIRED)
    private String productName;
    @Schema(description = "상품 가격", example = "1000", requiredMode = Schema.RequiredMode.REQUIRED)
    private int productPrice;
    @Schema(description = "상품 수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private int quantity;

}
