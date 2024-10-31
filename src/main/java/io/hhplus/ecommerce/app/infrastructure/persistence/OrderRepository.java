package io.hhplus.ecommerce.app.infrastructure.persistence;

import io.hhplus.ecommerce.app.exception.CustomException;
import io.hhplus.ecommerce.app.domain.model.Order;
import io.hhplus.ecommerce.app.domain.model.OrderItem;
import io.hhplus.ecommerce.app.domain.model.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void saveOrder(Order order) {
        entityManager.persist(order);
    }


    public Order getOrderInfo(Long orderId) {
        String query = "SELECT o FROM Order o WHERE o.id = :orderId";

        try {

            return entityManager.createQuery(query, Order.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "주문을 찾을 수 없습니다.");
        }
    }

    public List<OrderItem> getOrderItem(Long orderId) {

        String query = "SELECT oi FROM OrderItem oi WHERE oi.orderId = :orderId";

        List<OrderItem> items = entityManager.createQuery(
                        query, OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList();

        if (items.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "해당 주문에 대한 항목이 존재하지 않습니다.");
        }
        return items;
    }



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

    public int sumPaymentsByOrderId(Long orderId) {
        String query = "SELECT SUM(p.amount) FROM Payment p WHERE p.orderId = :orderId";
        Long result = entityManager.createQuery(query, Long.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
        if (result > Integer.MAX_VALUE) {
            throw new ArithmeticException("결제 금액의 합이 int 범위를 초과합니다.");
        }

        return result.intValue();
    }

    public void savePayment(Payment payment) {
        entityManager.persist(payment);
    }

    public void updatePaymentInfo(String txKey, String status) {
        String query = "UPDATE Payment p SET p.status = :status WHERE p.txKey = :txKey";
        int updated = entityManager.createQuery(query)
                .setParameter("status", status)
                .setParameter("txKey", txKey)
                .executeUpdate();

        if (updated != 1) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "주문 상태 업데이트에 실패했습니다.");
        }
    }

    }
