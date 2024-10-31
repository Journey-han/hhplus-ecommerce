package io.hhplus.ecommerce.app.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BondStatus {

    SETTLED("SETTLED"),
    EXPIRED("EXPIRED"),
    ISSUED("ISSUED");

    private final String message;
}
