package com.antony.nextrade.modules.users.usecase.findprofile;

import com.antony.nextrade.modules.users.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindProfileUseCase {
    private final IUserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileResponse execute(UUID id) {
        var user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        var role = user.getRole();
        return new ProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getUsername(),
                role.getId(), role.getCode());
    }
}
