package com.antony.nextrade.modules.auth.signin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInRequest(
        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        @Size(max = 255)
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String password
) {
}
