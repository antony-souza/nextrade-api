package com.antony.nextrade.config.scripts;

import com.antony.nextrade.config.security.PasswordPolicy;
import com.antony.nextrade.modules.auth.signup.SignUpRequest;
import com.antony.nextrade.modules.roles.model.RoleCodes;
import com.antony.nextrade.modules.roles.repository.IRoleRepository;
import com.antony.nextrade.modules.users.model.UserEntity;
import com.antony.nextrade.modules.users.repository.IUserRepository;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Component
@ConditionalOnProperty(name = "nextrade.seed.admin.enabled", havingValue = "true")
public class DefaultUserSeed implements ApplicationRunner {
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SignUpRequest admin;

    public DefaultUserSeed(
            IRoleRepository roleRepository,
            IUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            Validator validator,
            @Value("${nextrade.seed.admin.email}") String email,
            @Value("${nextrade.seed.admin.username}") String username,
            @Value("${nextrade.seed.admin.password}") String password
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        admin = new SignUpRequest("Administrador", email.strip().toLowerCase(Locale.ROOT),
                username.strip().toLowerCase(Locale.ROOT), password);

        if (!validator.validate(admin).isEmpty() || !PasswordPolicy.fitsBcrypt(password)) {
            throw new IllegalArgumentException("Configure email, username e senha válidos para a seed de administrador");
        }
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        var role = roleRepository.findByCodeForUpdate(RoleCodes.ADMIN)
                .orElseThrow(() -> new IllegalStateException("Role admin não encontrada"));

        var existing = userRepository.findByEmail(admin.email());
        if (existing.isPresent()) {
            var user = existing.get();
            if (user.getDeletedAt() == null && user.getRole().getId().equals(role.getId())
                    && user.getUsername().equals(admin.username())) {
                return;
            }
            throw new IllegalStateException("Email da seed já pertence a outro cadastro");
        }

        if (userRepository.existsByUsername(admin.username())) {
            throw new IllegalStateException("Username da seed já pertence a outro cadastro");
        }

        var user = new UserEntity();
        user.setName(admin.name());
        user.setEmail(admin.email());
        user.setUsername(admin.username());
        user.setPassword(passwordEncoder.encode(admin.password()));
        user.setRole(role);
        userRepository.save(user);
    }
}
