package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.exception.CustomException;
import io.hhplus.ecommerce.app.infrastructure.persistence.ProductStockRepository;
import io.hhplus.ecommerce.app.domain.model.ProductStock;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductStockService {

    private final ProductStockRepository productStockRepository;
    private final RedisTemplate<String, Object> redisTemplate;


    /**
     * 5.1 현재 재고 조회
     * @param productId
     * @return
     */
    public int getCurrentStock(Long productId) {
        return productStockRepository.getCurrentStock(productId);
    }


    /**
     * 5.2 재고 업데이트
     * @param productId
     * @param quantity
     */
    public void updateSaleProduct(Long productId, int quantity) {

        Optional<ProductStock> productStockOptional = productStockRepository.findByProductId(productId);
        ProductStock productStock = productStockOptional.orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "상품의 재고 정보를 찾을 수 없습니다."));
        productStock.updateStock(productId, quantity);
        productStockRepository.save(productStock);

        // 캐시 갱신
        redisTemplate.opsForValue().set("productStock:" + productId, productStock);
    }
}
