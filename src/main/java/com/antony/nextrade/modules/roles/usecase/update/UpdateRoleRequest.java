package com.antony.nextrade.modules.roles.usecase.update;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255)
        String name,

        @NotBlank(message = "O código é obrigatório")
        @Size(max = 255)
        String code,

        @NotNull(message = "O level é obrigatório")
        @Min(value = 0, message = "O level deve ser no mínimo 0")
        Integer level
) {
}
