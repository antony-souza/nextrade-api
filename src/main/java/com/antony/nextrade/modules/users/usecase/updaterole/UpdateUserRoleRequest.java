package com.antony.nextrade.modules.users.usecase.updaterole;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateUserRoleRequest(@NotNull(message = "O ID da role é obrigatório") UUID roleId) {
}
