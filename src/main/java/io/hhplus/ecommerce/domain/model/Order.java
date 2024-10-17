package io.hhplus.ecommerce.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "ORDERS")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String bondKey;

    private int totalPrice;

    private String status;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    protected Order() { }

    // 정적 팩토리 메서드로 객체 생성
    public static Order create(Long userId,String status) {
        Order order = new Order();
        order.userId = userId;
        order.status = status;
        order.createDate = LocalDateTime.now();
        return order;
    }

    // 비즈니스 메서드로 총 금액 설정
    public void updateTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void createBondKey(String bondKey) {
        this.bondKey = bondKey;
    }

    public void updateStatus(String status) {
        this.status = status;
        this.updateDate = LocalDateTime.now();
    }
}
