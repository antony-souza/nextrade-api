package com.antony.nextrade.modules.auth.signin;

import com.antony.nextrade.config.security.PasswordPolicy;
import com.antony.nextrade.config.security.TokenProvider;
import com.antony.nextrade.modules.users.model.UserEntity;
import com.antony.nextrade.modules.users.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class SignInUseCase {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional(readOnly = true)
    public SignInResponse execute(SignInRequest request) {
        if (!PasswordPolicy.fitsBcrypt(request.password())) {
            throw invalidCredentials();
        }

        UserEntity user = userRepository
                .findByEmailAndDeletedAtIsNullAndRole_DeletedAtIsNull(request.email().trim().toLowerCase(Locale.ROOT))
                .orElseThrow(this::invalidCredentials);

        boolean passwordMatch = passwordEncoder.matches(request.password(), user.getPassword());

        if (!passwordMatch) {
            throw invalidCredentials();
        }

        String token = tokenProvider.generateToken(user.getId());

        return new SignInResponse(token);
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos");
    }
}
