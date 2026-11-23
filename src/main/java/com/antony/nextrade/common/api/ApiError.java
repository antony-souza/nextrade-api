package com.antony.nextrade.common.api;

public record ApiError(
        String field,
        String message
) {
}
