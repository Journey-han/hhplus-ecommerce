package io.hhplus.ecommerce.app.application.response;

import lombok.Data;

@Data
public class BalanceResponse {
    private Long userId;
    private int totalBalance;

    public BalanceResponse(Long userId, int totalBalance) {
        this.userId = userId;
        this.totalBalance = totalBalance;
    }
}
