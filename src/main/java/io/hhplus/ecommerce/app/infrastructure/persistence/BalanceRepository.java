package io.hhplus.ecommerce.app.infrastructure.persistence;

import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class BalanceRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Balance getBalanceByUserId(Long userId) {

        String jpql = "SELECT b FROM Balance b WHERE b.userId = :userId";

        try {

            Balance balance = entityManager.createQuery(jpql, Balance.class)
                    .setParameter("userId", userId)
                    .getSingleResult();

            return balance;

        } catch (NoResultException e) {
            log.debug("사용자를 찾을 수 없습니다. Userid: {}", userId);
            return null;
        }
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Transactional
    public void save(Balance balance) {

        String jpql = "UPDATE Balance b SET b.totalBalance = :totalBalance WHERE b.userId = :userId";

        try {
            int updatedRows = entityManager.createQuery(jpql)
                    .setParameter("totalBalance", balance.getTotalBalance())
                    .setParameter("userId", balance.getUserId())
                    .executeUpdate();

            log.debug("잔액 저장 userId={}, updatedRows={}", balance.getUserId(), updatedRows);
        } catch (NoResultException e) {
            log.debug("사용자를 찾을 수 없습니다. Userid: {}", balance.getUserId());
        }
    }

}
