package io.hhplus.ecommerce;

import io.hhplus.ecommerce.app.application.request.BalanceRequest;
import io.hhplus.ecommerce.app.application.service.BalanceService;
import io.hhplus.ecommerce.app.domain.common.UserStatus;
import io.hhplus.ecommerce.app.domain.model.Balance;
import io.hhplus.ecommerce.app.application.response.BalanceResponse;
import io.hhplus.ecommerce.app.domain.model.User;
import io.hhplus.ecommerce.app.infrastructure.persistence.BalanceRepository;
import io.hhplus.ecommerce.app.infrastructure.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class BalanceServiceTest {

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private BalanceService balanceService;
    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void setUpBeforeAll() {
        System.out.println("테스트 시작-");
    }

    @BeforeEach
    @Transactional
    void setUpBeforeEach() {

        userRepository.save(new User(1001L, UserStatus.ACTIVATE.getMessage()));
        balanceRepository.save(new Balance(1L, 1001L, 5000));

        Balance savedBalance = balanceRepository.getBalanceByUserId(1001L);
        assertNotNull(savedBalance);
        assertThat(savedBalance.getUserId()).isEqualTo(1001L);
    }


    @Test
    @DisplayName("잔액 충전 성공 테스트")
    public void successBalanceTopUp() {

        BalanceRequest request = new BalanceRequest(10000);
        BalanceResponse response = balanceService.chargeBalance(1001L, request);

        assertThat(response).isNotNull();
        assertThat(response.getTotalBalance()).isEqualTo(15000); // 5000 + 10000 = 15000
    }

    @Test
    @DisplayName("잔액 조회 테스트")
    public void getBalanceTest() {

        BalanceResponse response = balanceService.getBalance(1001L);

        assertThat(response).isNotNull();
        assertThat(response.getTotalBalance()).isEqualTo(50000);

    }
}
