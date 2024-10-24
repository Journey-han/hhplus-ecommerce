package io.hhplus.ecommerce;

import io.hhplus.ecommerce.app.application.request.BalanceRequest;
import io.hhplus.ecommerce.app.application.service.BalanceService;
import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.application.response.BalanceResponse;
import io.hhplus.ecommerce.app.infrastructure.persistence.BalanceRepositoryImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class BalanceServiceTest {

    @Autowired
    private BalanceRepositoryImpl balanceRepository;

    @Autowired
    private BalanceService balanceService;

    @Test
    @DisplayName("잔액 충전 성공 테스트")
    public void successBalanceTopUp() {
        // 사용자 초기 잔액 설정
        balanceRepository.save(new Balance(1L, 1L, 5000));

        // 충전 요청
        BalanceRequest request = new BalanceRequest(1L, 10000);
        BalanceResponse response = balanceService.chargeBalance(request);

        // 검증
        assertThat(response).isNotNull();
        assertThat(response.getBalance()).isEqualTo(15000); // 5000 + 10000 = 15000
    }

    @Test
    @DisplayName("잔액 조회 테스트")
    public void getBalanceTest() {
        // 사용자 초기 잔액 설정
        balanceRepository.save(new Balance(1L, 1L, 5000));

        // 조회 요청
        BalanceResponse response = balanceService.getBalance(new BalanceRequest().getUserId());

        // 검증
        assertThat(response).isNotNull();
        assertThat(response.getBalance()).isEqualTo(5000);
    }
}
