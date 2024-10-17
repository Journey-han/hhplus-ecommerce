package io.hhplus.ecommerce.api.application.port.in;

import io.hhplus.ecommerce.dto.response.ProductResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductUseCase {

    List<ProductResponse> getAllProducts();
    List<ProductResponse> getPopularProducts(LocalDateTime sinceDate);
    ProductResponse getOneProducts(Long productId);
}
