package io.hhplus.ecommerce.api.application.service;

import io.hhplus.ecommerce.api.application.port.in.ProductStockUseCase;
import io.hhplus.ecommerce.api.application.port.out.ProductStockRepository;
import io.hhplus.ecommerce.domain.common.StockTransactionType;
import io.hhplus.ecommerce.domain.model.ProductStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductStockService implements ProductStockUseCase {

    private final ProductStockRepository productStockRepository;

    @Override
    public int getCurrentStock(Long productId) {
        return productStockRepository.getCurrentStock(productId);
    }

    @Override
    public void recordStockChange(Long productId, int quantity, StockTransactionType type) {
        int currentStock = getCurrentStock(productId);
        ProductStock stockRecord = new ProductStock(productId, quantity, type.getMessage(), currentStock + quantity);
        productStockRepository.save(stockRecord);
    }
}
