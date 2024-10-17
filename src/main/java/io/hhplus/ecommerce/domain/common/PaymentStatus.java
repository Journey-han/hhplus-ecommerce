package io.hhplus.ecommerce.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    WAITING(0, "WAITING"),
    PAID(1, "COMPLETED"),
    FAILED(-1, "FAILED");

    private final int status;
    private final String message;
}
