package com.antony.nextrade.modules.rolepermissions.usecase.create;

import com.antony.nextrade.config.security.AuthenticatedUser;
import com.antony.nextrade.modules.permissions.repository.IPermissionRepository;
import com.antony.nextrade.modules.rolepermissions.model.RolePermissionEntity;
import com.antony.nextrade.modules.rolepermissions.repository.IRolePermissionRepository;
import com.antony.nextrade.modules.rolepermissions.services.RolePermissionPolicy;
import com.antony.nextrade.modules.roles.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CreateRolePermissionUseCase {
    private final IRolePermissionRepository rolePermissionRepository;
    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;
    private final RolePermissionPolicy rolePermissionPolicy;

    @Transactional
    public CreateRolePermissionResponse execute(AuthenticatedUser actor, CreateRolePermissionRequest request) {
        var role = roleRepository.findByIdAndDeletedAtIsNull(request.roleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role não encontrada"));
        var permission = permissionRepository.findByIdAndDeletedAtIsNull(request.permissionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Permissão não encontrada"));
        rolePermissionPolicy.validateChange(actor, role, permission);

        var existing = rolePermissionRepository.findByRoleIdAndPermissionId(request.roleId(), request.permissionId());
        if (existing.isPresent() && existing.get().getDeletedAt() == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A role já possui essa permissão");
        }

        var link = existing.orElseGet(() -> new RolePermissionEntity(role, permission));
        link.setDeletedAt(null);
        var saved = rolePermissionRepository.save(link);
        return new CreateRolePermissionResponse(saved.getId(), "Permissão vinculada com sucesso");
    }
}
