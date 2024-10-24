package io.hhplus.ecommerce.app.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING(HttpStatus.ACCEPTED.value(), "PENDING"),
    COMPLETED(HttpStatus.CREATED.value(), "COMPLETED"),
    CANCELLED(HttpStatus.BAD_REQUEST.value(), "CANCELLED");

    private final int status;
    private final String message;

}
