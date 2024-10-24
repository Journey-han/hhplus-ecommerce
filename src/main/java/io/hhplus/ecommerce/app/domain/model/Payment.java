package io.hhplus.ecommerce.app.domain.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String txKey;

    private Long orderId;

    private int amount;

    private String paymentMethod;

    private String status;

    private LocalDateTime createDate;

    public Payment(String txKey, Long orderId, int amount, String paymentMethod, String status) {
        this.txKey = txKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.createDate = LocalDateTime.now();
    }

    public Payment() {

    }
}
