package com.antony.nextrade.modules.rolepermissions.usecase.delete;

import com.antony.nextrade.config.security.AuthenticatedUser;
import com.antony.nextrade.modules.rolepermissions.repository.IRolePermissionRepository;
import com.antony.nextrade.modules.rolepermissions.services.RolePermissionPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteRolePermissionUseCase {
    private final IRolePermissionRepository rolePermissionRepository;
    private final RolePermissionPolicy rolePermissionPolicy;

    @Transactional
    public void execute(AuthenticatedUser actor, UUID id) {
        var link = rolePermissionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vínculo não encontrado"));
        rolePermissionPolicy.validateChange(actor, link.getRole(), link.getPermission());
        link.setDeletedAt(LocalDateTime.now());
    }
}
