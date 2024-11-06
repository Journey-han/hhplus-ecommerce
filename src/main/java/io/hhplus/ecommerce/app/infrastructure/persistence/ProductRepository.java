package io.hhplus.ecommerce.app.infrastructure.persistence;

import io.hhplus.ecommerce.app.domain.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Product product) {
        entityManager.merge(product);
    }

    public List<Product> getAllProducts() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    public List<Product> getTop5BySalesSince(LocalDateTime sinceDate) {
        return entityManager.createQuery(
                        "SELECT p FROM Product p WHERE p.updateDate >= :sinceDate ORDER BY p.sales DESC", Product.class)
                .setParameter("sinceDate", sinceDate)
                .setMaxResults(5)
                .getResultList();
    }

    public Optional<Product> getOneProducts(Long productId) {
        return Optional.ofNullable(entityManager.find(Product.class, productId));
    }
}
