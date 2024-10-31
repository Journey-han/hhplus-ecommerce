package io.hhplus.ecommerce.app.application.request;

import io.hhplus.ecommerce.app.domain.model.Order;
import io.hhplus.ecommerce.app.domain.model.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "주문 요청")
public class OrderRequest {


    @Schema(
            description = "주문 항목 리스트",
            type = "array",
            implementation = OrderItemRequest.class,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<OrderItemRequest> items;

}