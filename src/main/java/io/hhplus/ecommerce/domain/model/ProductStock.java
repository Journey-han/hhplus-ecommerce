package io.hhplus.ecommerce.domain.model;

import io.hhplus.ecommerce.domain.common.StockTransactionType;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "PRODUCT_STOCK")
public class ProductStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long productId;

    private int quantity;  // 입출고 수량 (양수는 입고, 음수는 출고)

    private String productType;

    private int stock;

    private LocalDateTime updateDate;

    public ProductStock(Long productId, int quantity, String productType, int stock) {
        this.productId = productId;
        this.quantity = quantity;
        this.productType = productType;
        this.stock = stock;
        this.updateDate = LocalDateTime.now();
    }


    public ProductStock() {

    }
}
