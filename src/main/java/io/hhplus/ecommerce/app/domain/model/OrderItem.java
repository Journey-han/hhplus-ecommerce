package io.hhplus.ecommerce.app.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ORDERS_ITEM")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long orderId;

    private Long productId;

    private int quantity;

    LocalDateTime createDate;

    public OrderItem(Long productId, Long orderId, int quantity) {
        this.productId = productId;
        this.orderId = orderId;
        this.quantity = quantity;
        this.createDate = LocalDateTime.now();
    }

}
