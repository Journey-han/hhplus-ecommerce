package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.exception.BusinessException;
import io.hhplus.ecommerce.app.exception.ErrorCode;
import io.hhplus.ecommerce.app.domain.model.Product;
import io.hhplus.ecommerce.app.application.response.ProductResponse;
import io.hhplus.ecommerce.app.infrastructure.persistence.ProductRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepositoryImpl productRepository;

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.getAllProducts();
        return products.stream()
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getSales(), product.getUpdateDate()))
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getPopularProducts(LocalDateTime sinceDate) {
        List<Product> popularProducts = productRepository.getTop5BySalesSince(sinceDate);
        return popularProducts.stream()
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getSales(), product.getUpdateDate()))
                .collect(Collectors.toList());
    }

    public ProductResponse getOneProduct(Long productId) {
        Product product = productRepository.getOneProducts(productId).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getSales(), product.getUpdateDate());
    }
}
