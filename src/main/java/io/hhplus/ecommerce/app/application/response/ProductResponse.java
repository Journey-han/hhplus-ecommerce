package io.hhplus.ecommerce.app.application.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품 응답 객체")
public class ProductResponse {

    @Schema(description = "상품 ID", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "상품명", example = "노트북", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "상품 가격", example = "1500000", requiredMode = Schema.RequiredMode.REQUIRED)
    private int price;

    @Schema(description = "판매 수량", example = "200", requiredMode = Schema.RequiredMode.REQUIRED)
    private int sales;

    @Schema(description = "상품 업데이트 시간", example = "2024-10-18T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private String updateDate;

}