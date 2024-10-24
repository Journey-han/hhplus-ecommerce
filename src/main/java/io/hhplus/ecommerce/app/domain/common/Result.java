package io.hhplus.ecommerce.app.domain.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Result {

    OK(HttpStatus.OK.value(), "SUCCESS"),
    FAIL(HttpStatus.BAD_REQUEST.value(), "FAILED");

    private final int status;
    private final String message;

}
