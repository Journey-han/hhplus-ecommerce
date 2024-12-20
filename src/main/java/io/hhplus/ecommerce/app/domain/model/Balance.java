package io.hhplus.ecommerce.app.domain.model;

import io.hhplus.ecommerce.app.exception.CustomException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "BALANCE")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // IDENTITY 전략 사용
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total_balance", nullable = false, updatable = false)
    private int totalBalance;

    @Version
    private Integer version;

    public Balance(Long userId, int totalBalance ) {
        this.userId = userId;
        this.totalBalance = totalBalance;
    }


    public void addAmount(int addAmount) {
        this.totalBalance += addAmount;
    }

    public void minusAmount(int minusAmount) {
        if (this.totalBalance < minusAmount) {
            throw new CustomException(HttpStatus.BAD_REQUEST,
                    "잔액이 부족합니다. 현재 잔액: " + this.totalBalance);
        }
        this.totalBalance -= minusAmount;
    }
}
