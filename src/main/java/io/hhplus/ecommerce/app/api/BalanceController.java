package io.hhplus.ecommerce.app.api;

import io.hhplus.ecommerce.app.application.service.BalanceService;
import io.hhplus.ecommerce.app.application.request.BalanceRequest;
import io.hhplus.ecommerce.app.application.response.BalanceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController {

    private final BalanceService balanceService;

    @PostMapping("/charge")
    public ResponseEntity<BalanceResponse> chargeBalance(@RequestBody BalanceRequest request) {
        BalanceResponse response = balanceService.chargeBalance(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable("userId") Long userId) {
        BalanceResponse response = balanceService.getBalance(userId);
        return ResponseEntity.ok(response);
    }
}


