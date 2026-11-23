package com.antony.nextrade.modules.auth.signup;

import com.antony.nextrade.config.security.PasswordPolicy;
import com.antony.nextrade.config.security.TokenProvider;
import com.antony.nextrade.modules.roles.model.RoleCodes;
import com.antony.nextrade.modules.roles.repository.IRoleRepository;
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
public class SignUpUseCase {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public SignUpResponse execute(SignUpRequest request) {
        PasswordPolicy.validateNewPassword(request.password());

        String email = request.email().strip().toLowerCase(Locale.ROOT);
        String username = request.username().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este email já está cadastrado");
        }
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este username já está cadastrado");
        }

        var role = roleRepository.findByCodeAndDeletedAtIsNull(RoleCodes.USER)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Cadastro indisponível no momento"));

        var user = new UserEntity();
        user.setName(request.name().strip());
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(role);

        var created = userRepository.save(user);
        return new SignUpResponse("Conta criada com sucesso", tokenProvider.generateToken(created.getId()));
    }
}
