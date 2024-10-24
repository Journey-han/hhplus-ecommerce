package io.hhplus.ecommerce.app.infrastructure.persistence;

import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.exception.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
public class BalanceRepositoryImpl {
    @PersistenceContext
    private EntityManager entityManager;

    public Balance getBalanceByUserId(Long userId) {
        entityManager.clear();
        String jpql = "SELECT b FROM Balance b WHERE b.userId = :userId";
        return entityManager.createQuery(jpql, Balance.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }

    @Transactional
    public void save(Balance balance) {
        if (entityManager.contains(balance)) {
            entityManager.merge(balance);
        } else {
            entityManager.persist(balance);
        }
    }

}
