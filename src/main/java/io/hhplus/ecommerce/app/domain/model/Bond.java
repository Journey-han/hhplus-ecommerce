package io.hhplus.ecommerce.app.domain.model;

import io.hhplus.ecommerce.app.domain.common.BondStatus;
import io.hhplus.ecommerce.app.domain.common.RandomKeyGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "BOND")
public class Bond {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String bondKey;

    private long orderId;

    private String status;

    private int amount;

    private LocalDateTime issuedDate;

    private LocalDateTime settledDate;

    public Bond(Long orderId, int amount, String status) {
        this.orderId = orderId;
        this.bondKey = "BOND"+ RandomKeyGenerator.createRandomKey(12);
        this.amount = amount;
        this.status = status;
        this.issuedDate = LocalDateTime.now();
    }

    public void settle(String bondKey) {
        this.bondKey = bondKey;
        this.status = BondStatus.SETTLED.getMessage();
        this.settledDate = LocalDateTime.now();
    }
}
