package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.exception.BusinessException;
import io.hhplus.ecommerce.app.exception.CustomException;
import io.hhplus.ecommerce.app.exception.ErrorCode;
import io.hhplus.ecommerce.app.domain.model.Product;
import io.hhplus.ecommerce.app.application.response.ProductResponse;
import io.hhplus.ecommerce.app.infrastructure.persistence.ProductRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 4.1 모든 상품 조회
     * @return
     */
    @Cacheable(value = "productCache", key = "'allProducts'")
    public List<Product> getAllProducts() {
        List<Product> products = productRepository.getAllProducts();
        return products.stream()
                .map(product -> new Product(product.getId(), product.getName(), product.getPrice(), product.getSales(), product.getUpdateDate()))
                .collect(Collectors.toList());
    }


    /**
     * 4.2 특정 상품 조회
     * @param productId
     * @return
     */
    public Product getOneProduct(Long productId) {
        // Redis에서 캐시 확인
        Product product = (Product) redisTemplate.opsForValue().get("product:" + productId);
        if (product == null) {
            product = productRepository.getOneProducts(productId).orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));
            redisTemplate.opsForValue().set("product:" + productId, product);
        }
        return product;
    }


    /**
     * 4.3 인기 상품 조회
     * @param sinceDate
     * @return
     */
    @Cacheable(value = "popularProductsCache", key = "'popularProducts'")
    public List<Product> getPopularProducts(LocalDateTime sinceDate) {
        List<Product> popularProducts = productRepository.getTop5BySalesSince(sinceDate);
        return popularProducts.stream()
                .map(product -> new Product(product.getId(), product.getName(), product.getPrice(), product.getSales(), product.getUpdateDate()))
                .collect(Collectors.toList());
    }
}
