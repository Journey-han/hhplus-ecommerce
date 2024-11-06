package io.hhplus.ecommerce.app.api;

import io.hhplus.ecommerce.app.application.service.BalanceService;
import io.hhplus.ecommerce.app.application.request.BalanceRequest;
import io.hhplus.ecommerce.app.application.response.BalanceResponse;
import io.hhplus.ecommerce.app.domain.common.CommonApiResponse;
import io.hhplus.ecommerce.app.domain.common.Result;
import io.hhplus.ecommerce.app.domain.model.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class BalanceController {

    private final BalanceService balanceService;

    @PostMapping("/balance")
    public ResponseEntity<CommonApiResponse<?>> chargeBalance(@RequestHeader("userId") Long userId, @RequestBody BalanceRequest request) {

        Balance balance = balanceService.chargeBalance(userId, request);
        BalanceResponse balanceResponse = new BalanceResponse(balance.getUserId(), balance.getTotalBalance());
        CommonApiResponse<BalanceResponse> response = new CommonApiResponse<>(Result.OK.getStatus(), Result.OK.getMessage(), "포인트 충전에 성공했습니다!", balanceResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/balance")
    public ResponseEntity<CommonApiResponse<?>> getBalance(@RequestHeader("userId") Long userId) {
        Balance balance = balanceService.getBalance(userId);
        BalanceResponse balanceResponse = new BalanceResponse(balance.getUserId(), balance.getTotalBalance());
        CommonApiResponse<BalanceResponse> response = new CommonApiResponse<>(Result.OK.getStatus(), Result.OK.getMessage(), "고객님의 포인트를 조회합니다!", balanceResponse);

        return ResponseEntity.ok(response);
    }
}


