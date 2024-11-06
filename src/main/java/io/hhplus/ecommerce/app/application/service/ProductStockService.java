package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.domain.model.Product;
import io.hhplus.ecommerce.app.infrastructure.persistence.ProductRepository;
import io.hhplus.ecommerce.app.infrastructure.persistence.ProductStockRepository;
import io.hhplus.ecommerce.app.domain.common.StockTransactionType;
import io.hhplus.ecommerce.app.domain.model.ProductStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductStockService {

    private final ProductStockRepository productStockRepository;
    private final ProductRepository productRepository;


    /**
     * 5.1 현재 재고 조회
     * @param productId
     * @return
     */
    public int getCurrentStock(Long productId) {
        return productStockRepository.getCurrentStock(productId);
    }


    /**
     * 5.2 재고 업데이트 (
     * @param productId
     * @param quantity
     */
    public void updateSaleProduct(Long productId, int quantity) {
        Product product = new Product(productId, quantity);
        productRepository.save(product);
    }
}
