package io.hhplus.ecommerce.app.api;

import io.hhplus.ecommerce.app.application.service.ProductService;
import io.hhplus.ecommerce.app.application.response.ProductResponse;
import io.hhplus.ecommerce.app.domain.common.CommonApiResponse;
import io.hhplus.ecommerce.app.domain.common.Result;
import io.hhplus.ecommerce.app.domain.model.Product;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Product API", description = "상품 관련 API")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "전체 상품 조회", description = "전체 상품을 조회합니다.")
    public ResponseEntity<CommonApiResponse<?>> getAllProducts() {
        List<Product> productList = productService.getAllProducts();
        List<ProductResponse> productListResponseList = productList.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getSales(),
                        product.getUpdateDate()
                ))
                .collect(Collectors.toList());
        CommonApiResponse<List<ProductResponse>> response = new CommonApiResponse<>(Result.OK.getStatus(), Result.OK.getMessage(), "상품이 성공적으로 조회되었습니다! 즐거운 쇼핑하세요~", productListResponseList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{productId}")
    @Operation(summary = "특정 상품 조회", description = "상품 ID로 상품 정보를 조회합니다.")
    public ResponseEntity<CommonApiResponse<?>> getOneProduct(
            @Parameter(description = "조회할 상품의 ID", example = "1001")
            @PathVariable(name = "productId") Long productId) {
        Product products = productService.getOneProduct(productId);
        ProductResponse productResponse = new ProductResponse(products.getId(), products.getName(), products.getSales(), products.getPrice(), products.getUpdateDate());
        CommonApiResponse<ProductResponse> response = new CommonApiResponse<>(Result.OK.getStatus(), Result.OK.getMessage(), "검색하신 상품이 조회되었습니다! 즐거운 쇼핑하세요~", productResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(summary = "인기 상품 조회", description = "최근 3일간 잘 팔린 상위 상품 5개를 조회합니다.")
    public ResponseEntity<CommonApiResponse<?>> getPopularProducts() {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(3);
        List<Product> popularProducts = productService.getPopularProducts(sinceDate);
        List<ProductResponse> popularProductsResponse = popularProducts.stream()
                .map(product -> new ProductResponse(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getSales(),
                        product.getUpdateDate()
                ))
                .collect(Collectors.toList());
        CommonApiResponse<List<ProductResponse>> response = new CommonApiResponse<>(Result.OK.getStatus(), Result.OK.getMessage(), "3일동안 가장 잘 팔린 상품 TOP 5입니다! 즐거운 쇼핑하세요~", popularProductsResponse);

        return ResponseEntity.ok(response);
    }
}
