package com.antony.nextrade.modules.permissions.usecase.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePermissionRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255)
        String name,

        @NotBlank(message = "O código é obrigatório")
        @Size(max = 255)
        String code,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 255)
        String description
) {
}
