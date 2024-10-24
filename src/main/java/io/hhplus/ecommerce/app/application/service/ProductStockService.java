package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.infrastructure.persistence.ProductStockRepository;
import io.hhplus.ecommerce.app.domain.common.StockTransactionType;
import io.hhplus.ecommerce.app.domain.model.ProductStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductStockService {

    private final ProductStockRepository productStockRepository;

    public int getCurrentStock(Long productId) {
        return productStockRepository.getCurrentStock(productId);
    }

    public void recordStockChange(Long productId, int quantity, StockTransactionType type) {
        int currentStock = getCurrentStock(productId);
        ProductStock stockRecord = new ProductStock(productId, quantity, type.getMessage(), currentStock + quantity);
        productStockRepository.save(stockRecord);
    }
}
