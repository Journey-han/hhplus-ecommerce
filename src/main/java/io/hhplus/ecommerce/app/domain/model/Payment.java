package io.hhplus.ecommerce.app.domain.model;

import io.hhplus.ecommerce.app.domain.common.RandomKeyGenerator;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

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

    private String status;

    private LocalDateTime createDate;

    public Payment(Long orderId, int amount, String status) {
        this.txKey = "TX"+ RandomKeyGenerator.createRandomKey(12);
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.createDate = LocalDateTime.now();
    }

    public Payment() {

    }
}
