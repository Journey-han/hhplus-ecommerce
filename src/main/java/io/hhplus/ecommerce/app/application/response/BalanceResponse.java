package io.hhplus.ecommerce.app.application.response;

import lombok.Data;

@Data
public class BalanceResponse {
    private Long userId;
    private int balance;

    public BalanceResponse(Long userId, int balance) {
        this.userId = userId;
        this.balance = balance;
    }
}
