package io.hhplus.ecommerce;

import io.hhplus.ecommerce.app.application.service.ProductService;
import io.hhplus.ecommerce.app.application.response.ProductResponse;
import io.hhplus.ecommerce.app.infrastructure.persistence.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProductServiceTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("상품 목록 조회 테스트")
    public void getAllProductsTest() {

        // 상품 조회
        List<ProductResponse> products = productService.getAllProducts();

        // 검증
        assertThat(products).hasSize(2);
    }

    @Test
    @DisplayName("인기 상품 조회 테스트")
    public void getPopularProductsTest() {
        LocalDateTime dateTime = LocalDateTime.now();

        List<ProductResponse> popularProducts = productService.getPopularProducts(dateTime);

        // 검증
        assertThat(popularProducts).hasSize(2);
    }
}
