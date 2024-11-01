package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.domain.common.UserStatus;
import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.application.request.BalanceRequest;
import io.hhplus.ecommerce.app.application.response.BalanceResponse;
import io.hhplus.ecommerce.app.domain.model.User;
import io.hhplus.ecommerce.app.exception.CustomException;
import io.hhplus.ecommerce.app.infrastructure.persistence.BalanceRepository;
import io.hhplus.ecommerce.app.infrastructure.persistence.UserRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final UserRepository userRepository;

    public BalanceService(BalanceRepository balanceRepository, UserRepository userRepository) {
        this.balanceRepository = balanceRepository;
        this.userRepository = userRepository;
    }


    /**
     * 1.1 잔액 조회
     * @param userId
     * @return
     */
    public BalanceResponse getBalance(Long userId) {
        log.info("Received request to get balance for userId={}", userId);

        User user = userRepository.findByUserId(userId);
        if (user == null || Objects.equals(user.getStatus(), UserStatus.DEACTIVATE.getMessage())) {
            log.info("User not found for userId={}", userId);
            throw new CustomException(HttpStatus.NOT_FOUND, "고객 정보를 찾을 수 없습니다. 요청한 userId=" + userId);
        } else {
            Balance balance = balanceRepository.getBalanceByUserId(userId);
            if (balance == null) {
                log.error("No balance found for userId={}", userId);
                throw new CustomException(HttpStatus.BAD_REQUEST, "해당 고객에 대한 잔액 정보를 찾을 수 없습니다.");
            }

            log.info("Balance for userId={} is {}", userId, balance.getTotalBalance());

            BalanceResponse response = new BalanceResponse(balance.getUserId(), balance.getTotalBalance());
            log.info("Response generated for userId={}, totalBalance={}", response.getUserId(), response.getTotalBalance());

            return response;
        }
    }


    /**
     * 1.2 잔액 충전
     * @param request
     * @return
     */
    @Retryable(
            value = {OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    @Transactional
    public BalanceResponse chargeBalance(Long userId, BalanceRequest request) {
        log.info("잔액 충전 요청 사항: userId={}, amount={}", userId, request.getAmount());
        try {
            User user = userRepository.findByUserId(userId);
            if (user == null || Objects.equals(user.getStatus(), UserStatus.DEACTIVATE.getMessage())) {
                log.info("고객 정보를 찾을 수 없습니다. userId={}", userId);
                throw new CustomException(HttpStatus.NOT_FOUND, "고객 정보를 찾을 수 없습니다.");
            } else {
                Balance balance = balanceRepository.getBalanceByUserId(userId);
                if (balance == null) {
                    log.error("해당 고객에 대한 잔액 정보를 찾을 수 없습니다.userId={}", userId);
                    throw new CustomException(HttpStatus.BAD_REQUEST, "해당 고객에 대한 잔액 정보를 찾을 수 없습니다.");
                }
                log.info("현재 userId={} 고객님의 잔액 정보는 {} 입니다.", userId, balance.getTotalBalance());

                balance.addAmount(request.getAmount());
                log.info("userId={}님이 요청한 {} 포인트가 충전되었습니다.", userId, balance.getTotalBalance());

                balanceRepository.save(balance);

                BalanceResponse response = new BalanceResponse(balance.getUserId(), balance.getTotalBalance());

                return response;
            }
        }catch (OptimisticLockException e) {
                throw new CustomException(HttpStatus.CONFLICT, "충돌이 발생!");
        }
    }

}
