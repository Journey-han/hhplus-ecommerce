package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.application.request.BalanceRequest;
import io.hhplus.ecommerce.app.application.response.BalanceResponse;
import io.hhplus.ecommerce.app.infrastructure.persistence.BalanceRepositoryImpl;
import org.springframework.stereotype.Service;

@Service
public class BalanceService {

    private final BalanceRepositoryImpl balanceRepository;

    public BalanceService(BalanceRepositoryImpl balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public BalanceResponse chargeBalance(BalanceRequest request) {
        Balance balance = balanceRepository.getBalanceByUserId(request.getUserId());
        balance.addAmount(request.getAmount());
        balanceRepository.save(balance);
        return new BalanceResponse(balance.getUserId(), balance.getAmount());
    }

    public BalanceResponse getBalance(Long userId) {
        Balance balance = balanceRepository.getBalanceByUserId(userId);
        return new BalanceResponse(balance.getUserId(), balance.getAmount());
    }
}
