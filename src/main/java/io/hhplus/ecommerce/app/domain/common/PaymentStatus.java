package io.hhplus.ecommerce.app.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    WAITING(HttpStatus.ACCEPTED.value(), "WAITING"),
    PAID(HttpStatus.CREATED.value(), "COMPLETED"),
    FAILED(HttpStatus.BAD_REQUEST.value(), "FAILED");

    private final int status;
    private final String message;
}
