package io.hhplus.ecommerce.infrastructure.persistence;

import io.hhplus.ecommerce.api.application.port.out.OrderRepository;
import io.hhplus.ecommerce.domain.common.CustomException;
import io.hhplus.ecommerce.domain.model.Order;
import io.hhplus.ecommerce.domain.model.Payment;
import io.hhplus.ecommerce.dto.response.OrderResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class OrderRepositoryImpl implements OrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveOrder(Order order) {
        entityManager.persist(order);
    }

    @Override
    public OrderResponse getOrderInfo(Long orderId) {
        String query = "SELECT o.id, o.userId, o.totalPrice, o.status, o.createDate, o.updateDate FROM Order o WHERE o.id = :orderId";

        try {

            return entityManager.createQuery(query, OrderResponse.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "주문을 찾을 수 없습니다.");
        }
    }

    @Override
    public int sumPaymentsByOrderId(Long orderId) {
        String query = "SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.orderId = :orderId";
        Integer result = entityManager.createQuery(query, Integer.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
        return result != null ? result : 0;
    }

    @Override
    public void savePayment(Payment payment) {
        entityManager.persist(payment);
    }

    @Override
    public void updateOrderInfo(Long orderId, String status) {

        String query = "UPDATE Order o SET o.status = :status WHERE o.id = :orderId";
        int updated = entityManager.createQuery(query)
                .setParameter("status", status)
                .setParameter("orderId", orderId)
                .executeUpdate();

        if (updated != 1) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "주문 상태 업데이트에 실패했습니다.");
        }
    }

}
