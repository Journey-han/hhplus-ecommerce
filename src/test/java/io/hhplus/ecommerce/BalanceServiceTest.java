package io.hhplus.ecommerce;

import io.hhplus.ecommerce.app.application.request.BalanceRequest;
import io.hhplus.ecommerce.app.application.service.BalanceService;
import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.application.response.BalanceResponse;
import io.hhplus.ecommerce.app.infrastructure.persistence.BalanceRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application.properties")
public class BalanceServiceTest {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private BalanceService balanceService;

    @Test
    @DisplayName("잔액 충전 성공 테스트")
    public void successBalanceTopUp() {
        // 사용자 초기 잔액 설정
        balanceRepository.save(new Balance(1L, 1001L, 5000));

        // 충전 요청
        BalanceRequest request = new BalanceRequest(10000);
        BalanceResponse response = balanceService.chargeBalance(1001L, request);

        // 검증
        assertThat(response).isNotNull();
        assertThat(response.getTotalBalance()).isEqualTo(15000); // 5000 + 10000 = 15000
    }

    @Test
    @DisplayName("잔액 조회 테스트")
    public void getBalanceTest() {
        // 사용자 초기 잔액 설정
        balanceRepository.save(new Balance(1L, 1001L, 5000));

        // 조회 요청
        BalanceResponse response = balanceService.getBalance(1000L);

        // 검증
        assertThat(response).isNotNull();
        assertThat(response.getTotalBalance()).isEqualTo(50000);

    }
}
