package com.antony.nextrade.config.security;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        UUID roleId,
        String roleCode
) {
}
