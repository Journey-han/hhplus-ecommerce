package io.hhplus.ecommerce.api.application.port.out;

import io.hhplus.ecommerce.domain.model.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductStockRepository extends JpaRepository<ProductStock, Long> {
    @Query("SELECT SUM(ps.quantity) FROM ProductStock ps WHERE ps.productId = :productId")
    int getCurrentStock(@Param("productId") Long productId);
}
