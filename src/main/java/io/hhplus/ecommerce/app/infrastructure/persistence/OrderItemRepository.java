package io.hhplus.ecommerce.app.infrastructure.persistence;

import io.hhplus.ecommerce.app.domain.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
}
