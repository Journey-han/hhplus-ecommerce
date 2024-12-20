package io.hhplus.ecommerce.app.infrastructure.persistence;

import io.hhplus.ecommerce.app.domain.model.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {

    @Query("SELECT ps FROM ProductStock ps WHERE ps.productId = :productId")
    int getCurrentStock(@Param("productId") Long productId);

    // 특정 상품의 최신 재고 내역 조회
    @Query("SELECT ps FROM ProductStock ps WHERE ps.productId = :productId ORDER BY ps.updateDate DESC")
    Optional<ProductStock> findByProductId(@Param("productId") Long productId);
}
