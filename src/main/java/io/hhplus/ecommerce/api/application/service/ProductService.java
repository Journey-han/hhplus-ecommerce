package io.hhplus.ecommerce.api.application.service;

import io.hhplus.ecommerce.api.application.port.in.ProductUseCase;
import io.hhplus.ecommerce.api.application.port.out.ProductRepository;
import io.hhplus.ecommerce.domain.model.Product;
import io.hhplus.ecommerce.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService implements ProductUseCase {

    private final ProductRepository productRepository;

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.getAllProducts();
        return products.stream()
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getSales(), product.getUpdateDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getPopularProducts(LocalDateTime sinceDate) {
        List<Product> popularProducts = productRepository.getTop5BySalesSince(sinceDate);
        return popularProducts.stream()
                .map(product -> new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getSales(), product.getUpdateDate()))
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse getOneProducts(Long productId) {
        Product product = productRepository.getOneProducts(productId);
        return new ProductResponse(product.getId(), product.getName(), product.getPrice(), product.getSales(), product.getUpdateDate());
    }
}
