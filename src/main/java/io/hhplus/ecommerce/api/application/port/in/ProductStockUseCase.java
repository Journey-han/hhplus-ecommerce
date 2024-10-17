package io.hhplus.ecommerce.api.application.port.in;

import io.hhplus.ecommerce.domain.common.StockTransactionType;

public interface ProductStockUseCase {

    int getCurrentStock(Long productId);
    void recordStockChange(Long productId, int quantity, StockTransactionType type);

}
