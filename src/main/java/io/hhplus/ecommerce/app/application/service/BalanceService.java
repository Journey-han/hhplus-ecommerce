package io.hhplus.ecommerce.app.application.service;

import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.application.request.BalanceRequest;
import io.hhplus.ecommerce.app.application.response.BalanceResponse;
import io.hhplus.ecommerce.app.domain.model.User;
import io.hhplus.ecommerce.app.exception.CustomException;
import io.hhplus.ecommerce.app.infrastructure.persistence.BalanceRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }


    /**
     * 1.1 잔액 조회
     * @param userId
     * @return
     */
    public BalanceResponse getBalance(Long userId) {
        log.info("Received request to get balance for userId={}", userId);

        User user = balanceRepository.getUserStatusById(userId);
        if (user == null) {
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
    @Transactional
    public BalanceResponse chargeBalance(Long userId, BalanceRequest request) {
        log.info("Received request to charge balance: userId={}, amount={}", userId, request.getAmount());

        User user = balanceRepository.getUserStatusById(userId);
        if (user == null) {
            log.info("User not found for userId={}", userId);
            throw new CustomException(HttpStatus.NOT_FOUND, "고객 정보를 찾을 수 없습니다. 요청한 userId=" + userId);
        } else {
            // 잔액 조회
            Balance balance = balanceRepository.getBalanceByUserId(userId);
            if (balance == null) {
                log.error("No balance found for userId={}", userId);
                throw new CustomException(HttpStatus.BAD_REQUEST, "해당 고객에 대한 잔액 정보를 찾을 수 없습니다.");
            }
            log.info("Current balance for userId={} is {}", userId, balance.getTotalBalance());

            // 잔액 추가
            balance.addAmount(request.getAmount());
            log.info("Updated balance for userId={} is {}", userId, balance.getTotalBalance());

            // 데이터 저장
            balanceRepository.save(balance);
            log.info("Balance saved for userId={}", userId);

            // 응답 생성
            BalanceResponse response = new BalanceResponse(balance.getUserId(), balance.getTotalBalance());
            log.info("Response generated for userId={}, totalBalance={}", response.getUserId(), response.getTotalBalance());

            return response;

        }
    }

}
