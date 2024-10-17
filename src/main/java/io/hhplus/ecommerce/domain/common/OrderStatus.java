package io.hhplus.ecommerce.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING(0, "PENDING"),
    COMPLETED(1, "COMPLETED"),
    CANCELLED(-1, "CANCELLED");

    private final int status;
    private final String message;

}
