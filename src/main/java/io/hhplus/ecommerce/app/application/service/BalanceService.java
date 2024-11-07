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
    public Balance getBalance(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null || Objects.equals(user.getStatus(), UserStatus.DEACTIVATE.getMessage())) {
            throw new CustomException(HttpStatus.NOT_FOUND, "고객 정보를 찾을 수 없습니다. 요청한 userId=" + userId);
        } else {
            Balance balance = balanceRepository.getBalanceByUserId(userId);
            if (balance == null) {
                log.error("해당 고객에 대한 잔액 정보를 찾을 수 없습니다.userId={}", userId);
                throw new CustomException(HttpStatus.BAD_REQUEST, "해당 고객에 대한 잔액 정보를 찾을 수 없습니다.");
            }

            return new Balance(balance.getUserId(), balance.getTotalBalance());
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
    public Balance chargeBalance(Long userId, BalanceRequest request) {
        try {
            User user = userRepository.findByUserId(userId);
            if (user == null || Objects.equals(user.getStatus(), UserStatus.DEACTIVATE.getMessage())) {
                throw new CustomException(HttpStatus.NOT_FOUND, "고객 정보를 찾을 수 없습니다.");
            } else {
                Balance balance = balanceRepository.getBalanceByUserId(userId);
                if (balance == null) {
                    log.error("해당 고객에 대한 잔액 정보를 찾을 수 없습니다.userId={}", userId);
                    throw new CustomException(HttpStatus.BAD_REQUEST, "해당 고객에 대한 잔액 정보를 찾을 수 없습니다.");
                }

                balance.addAmount(request.getAmount());

                balanceRepository.save(balance);

                return new Balance(balance.getUserId(), balance.getTotalBalance());
            }
        }catch (OptimisticLockException e) {
                throw new CustomException(HttpStatus.CONFLICT, "충돌이 발생!");
        }
    }

    /**
     * 1.3 잔액 차감
     * @return
     */
    @Transactional
    public Balance deductBalance(Long userId, int amount) {
        try {
            User user = userRepository.findByUserId(userId);
            if (user == null || Objects.equals(user.getStatus(), UserStatus.DEACTIVATE.getMessage())) {
                throw new CustomException(HttpStatus.NOT_FOUND, "고객 정보를 찾을 수 없습니다.");
            } else {
                Balance balance = balanceRepository.getBalanceByUserId(userId);
                if (balance == null) {
                    log.error("해당 고객에 대한 잔액 정보를 찾을 수 없습니다.userId={}", userId);
                    throw new CustomException(HttpStatus.BAD_REQUEST, "해당 고객에 대한 잔액 정보를 찾을 수 없습니다.");
                }

                balance.minusAmount(amount);

                balanceRepository.save(balance);

                return new Balance(balance.getUserId(), balance.getTotalBalance());
            }
        }catch (OptimisticLockException e) {
            throw new CustomException(HttpStatus.CONFLICT, "충돌이 발생!");
        }
    }

}
