package io.hhplus.ecommerce.app.infrastructure.persistence;


import io.hhplus.ecommerce.app.domain.model.Bond;
import io.hhplus.ecommerce.app.exception.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
public class BondRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void save(Bond bond) {
        entityManager.persist(bond);
    }

    public Bond findByOrderId(Long orderId) {
        try {
            return entityManager.createQuery("SELECT b FROM Bond b WHERE b.orderId = :orderId", Bond.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new CustomException(HttpStatus.NOT_FOUND, "해당 주문의 채권이 존재하지 않습니다.");
        }
    }
}
