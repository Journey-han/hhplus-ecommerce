package io.hhplus.ecommerce.app.infrastructure.persistence;

import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class BalanceRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public User getUserStatusById(Long userId) {
        log.debug("Querying for user status by id: {}", userId);

        String jpql = "SELECT u FROM User u WHERE u.id = :userId";

        try {

            User user = entityManager.createQuery(jpql, User.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
            log.debug("Queried balance: userId={}, status={}", userId, user.getStatus());

            return user;

        } catch (NoResultException e) {
            log.warn("No user found for id: {}", userId);
            return null;
        }

    }

    public Balance getBalanceByUserId(Long userId) {
        log.debug("Querying balance for userId={}", userId);

        String jpql = "SELECT b FROM Balance b WHERE b.userId = :userId";

        try {

            Balance balance = entityManager.createQuery(jpql, Balance.class)
                    .setParameter("userId", userId)
                    .getSingleResult();

            log.debug("Queried balance: userId={}, totalBalance={}", balance.getUserId(), balance.getTotalBalance());
            return balance;

        } catch (NoResultException e) {
            log.warn("No user found for id: {}", userId);
            return null;
        }
    }

    @Transactional
    public void save(Balance balance) {
        log.debug("Saving balance for userId={}, totalBalance={}", balance.getUserId(), balance.getTotalBalance());

        String jpql = "UPDATE Balance b SET b.totalBalance = :totalBalance WHERE b.userId = :userId";

        try {
            int updatedRows = entityManager.createQuery(jpql)
                    .setParameter("totalBalance", balance.getTotalBalance())
                    .setParameter("userId", balance.getUserId())
                    .executeUpdate();

            log.debug("Balance saved for userId={}, updatedRows={}", balance.getUserId(), updatedRows);
        } catch (NoResultException e) {
            log.warn("No user found for id: {}", balance.getUserId());
        }
    }

}
