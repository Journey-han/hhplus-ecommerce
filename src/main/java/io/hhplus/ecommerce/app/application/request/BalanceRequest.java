package io.hhplus.ecommerce.app.application.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class BalanceRequest {

    private Long userId;
    private int amount;


    public BalanceRequest(long userId) {
        this.userId = userId;
        this.amount = getAmount();
    }
}
