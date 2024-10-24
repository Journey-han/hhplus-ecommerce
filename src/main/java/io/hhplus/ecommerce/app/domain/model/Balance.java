package io.hhplus.ecommerce.app.domain.model;

import io.hhplus.ecommerce.app.exception.CustomException;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Entity
@Table(name = "BALANCE")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "USER_ID")
    private Long userId;
    private int amount;

    public Balance(Long id, Long userId, int amount ) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
    }

    protected Balance() {}

    public void addAmount(int addAmount) {
        this.amount += addAmount;
    }

    public void minusAmount(int minusAmount) {
        if (this.amount < minusAmount) {
            throw new CustomException(HttpStatus.BAD_REQUEST,
                    "잔액이 부족합니다. 현재 잔액: " + this.amount);
        }
        this.amount -= minusAmount;
    }
}
