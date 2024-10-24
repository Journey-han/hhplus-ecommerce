package io.hhplus.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class HhplusEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HhplusEcommerceApplication.class, args);
    }

}
