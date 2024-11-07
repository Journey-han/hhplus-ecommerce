package io.hhplus.ecommerce.app.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "PRODUCT_STOCK")
public class ProductStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long productId;

    private int stock;

    private String updateDate;

    public ProductStock(Long productId, int stock) {
        this.productId = productId;
        this.stock = stock;
        this.updateDate = String.valueOf(LocalDateTime.now());
    }

    // 재고 예약 (출고)
    public void reserveStock(int quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stock -= quantity; // 임시 재고 차감
    }

    // 재고 해제 (예약 취소 시)
    public void releaseStock(int quantity) {
        this.stock += quantity;
        this.updateDate = String.valueOf(LocalDateTime.now());
    }

    // 재고 업데이트
    public void updateStock(Long productId, int quantity) {
        this.productId = productId;
        this.stock+=quantity;
        this.updateDate = String.valueOf(LocalDateTime.now());
    }
}
