package io.hhplus.ecommerce.dto.request;

import io.hhplus.ecommerce.domain.model.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 요청")
public class OrderRequest {

    @Schema(description = "사용자 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(
            description = "주문 항목 리스트",
            type = "array",
            implementation = OrderItemRequest.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<OrderItemRequest> items;

    public <E> OrderRequest(long userId, List<E> items) {
    }
}