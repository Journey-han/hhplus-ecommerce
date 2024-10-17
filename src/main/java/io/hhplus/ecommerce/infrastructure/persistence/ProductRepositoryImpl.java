package io.hhplus.ecommerce.infrastructure.persistence;

import io.hhplus.ecommerce.api.application.port.out.ProductRepository;
import io.hhplus.ecommerce.domain.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Product> getAllProducts() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    @Override
    public List<Product> getTop5BySalesSince(LocalDateTime sinceDate) {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.updateDate >= :sinceDate ORDER BY p.sales DESC", Product.class)
                .setParameter("sinceDate", sinceDate)
                .setMaxResults(5)
                .getResultList();
    }

    @Override
    public Product getOneProducts(Long productId) {
        return entityManager.find(Product.class, productId);
    }
}
