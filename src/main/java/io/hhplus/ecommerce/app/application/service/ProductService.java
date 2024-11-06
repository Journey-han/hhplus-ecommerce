package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.exception.BusinessException;
import io.hhplus.ecommerce.app.exception.ErrorCode;
import io.hhplus.ecommerce.app.domain.model.Product;
import io.hhplus.ecommerce.app.application.response.ProductResponse;
import io.hhplus.ecommerce.app.infrastructure.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 4.1 모든 상품 조회
     * @return
     */
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.getAllProducts();
        return products.stream()
                .map(product -> new Product(product.getId(), product.getName(), product.getPrice(), product.getSales()))
                .collect(Collectors.toList());
    }


    /**
     * 4.2 특정 상품 조회
     * @param productId
     * @return
     */
    public Product getOneProduct(Long productId) {
        Product product = productRepository.getOneProducts(productId).orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        return new Product(product.getId(), product.getName(), product.getPrice(), product.getSales());
    }


    /**
     * 4.3 인기 상품 조회
     * @param sinceDate
     * @return
     */
    public List<Product> getPopularProducts(LocalDateTime sinceDate) {
        List<Product> popularProducts = productRepository.getTop5BySalesSince(sinceDate);
        return popularProducts.stream()
                .map(product -> new Product(product.getId(), product.getName(), product.getPrice(), product.getSales()))
                .collect(Collectors.toList());
    }
}
