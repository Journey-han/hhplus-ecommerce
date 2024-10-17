package io.hhplus.ecommerce.api.application.port.out;

import io.hhplus.ecommerce.domain.model.Product;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository {

    List<Product> getAllProducts();
    List<Product> getTop5BySalesSince(LocalDateTime sinceDate);
    Product getOneProducts(Long productId);
}
