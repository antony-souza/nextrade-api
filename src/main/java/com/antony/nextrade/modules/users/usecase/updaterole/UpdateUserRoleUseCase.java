package com.antony.nextrade.modules.users.usecase.updaterole;

import com.antony.nextrade.modules.roles.model.RoleCodes;
import com.antony.nextrade.modules.roles.repository.IRoleRepository;
import com.antony.nextrade.modules.users.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateUserRoleUseCase {
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Transactional
    public void execute(UUID userId, UUID roleId) {
        var user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        var role = roleRepository.findByIdAndDeletedAtIsNull(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role não encontrada"));
        if (RoleCodes.ADMIN.equals(user.getRole().getCode()) || RoleCodes.ADMIN.equals(role.getCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "A role admin é atribuída pela seed");
        }
        user.setRole(role);
    }
}
