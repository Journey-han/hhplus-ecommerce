package io.hhplus.ecommerce.app.api;

import io.hhplus.ecommerce.app.application.service.ProductService;
import io.hhplus.ecommerce.app.application.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product API", description = "상품 관련 API")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "전체 상품 조회", description = "전체 상품을 조회합니다.")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("{productId}")
    @Operation(summary = "특정 상품 조회", description = "상품 ID로 상품 정보를 조회합니다.")
    public ResponseEntity<ProductResponse> getOneProduct(
            @Parameter(description = "조회할 상품의 ID", example = "1001")
            @PathVariable(name = "productId") Long productId) {
        ProductResponse products = productService.getOneProduct(productId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 상품 조회", description = "최근 3일간 잘 팔린 상위 상품 5개를 조회합니다.")
    public ResponseEntity<List<ProductResponse>> getPopularProducts() {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(3);
        List<ProductResponse> popularProducts = productService.getPopularProducts(sinceDate);
        return ResponseEntity.ok(popularProducts);
    }
}
