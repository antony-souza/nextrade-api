package com.antony.nextrade.modules.auth.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 255)
        String name,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email deve ser válido")
        @Size(max = 255)
        String email,

        @NotBlank(message = "O username é obrigatório")
        @Pattern(regexp = "^[a-zA-Z0-9._]{3,30}$", message = "O usuário deve ter de 3 a 30 caracteres: letras, números, ponto ou _")
        String username,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
        String password
) {
}
