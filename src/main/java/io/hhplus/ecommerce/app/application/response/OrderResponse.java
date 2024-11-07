package io.hhplus.ecommerce.app.application.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "주문 응답")
public class OrderResponse {
    @Schema(description = "주문 ID", example = "11", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "총 주문 금액", example = "2000", requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalPrice;

    @Schema(description = "주문 상태", example = "COMPLETED", requiredMode = Schema.RequiredMode.REQUIRED)
    private String status;

    @Schema(
            description = "주문 항목 리스트",
            type = "array",         // 배열로 인식되도록 명시
            implementation = OrderItemResponse.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<OrderItemResponse> items;

    @Schema(description = "주문 생성 시간", example = "2024-10-18T12:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createDate;


}