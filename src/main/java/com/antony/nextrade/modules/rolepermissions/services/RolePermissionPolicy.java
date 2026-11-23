package com.antony.nextrade.modules.rolepermissions.services;

import com.antony.nextrade.config.security.AuthenticatedUser;
import com.antony.nextrade.modules.permissions.model.PermissionEntity;
import com.antony.nextrade.modules.rolepermissions.repository.IRolePermissionRepository;
import com.antony.nextrade.modules.roles.model.RoleCodes;
import com.antony.nextrade.modules.roles.model.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RolePermissionPolicy {
    private final IRolePermissionRepository repository;

    public void validateChange(AuthenticatedUser actor, RoleEntity role, PermissionEntity permission) {
        if (RoleCodes.ADMIN.equals(role.getCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Os vínculos da role admin são definidos pelas migrations");
        }

        if (!RoleCodes.ADMIN.equals(actor.roleCode())
                && !repository.existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
                        actor.roleId(), permission.getCode())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você precisa ter a permissão que deseja gerenciar");
        }
    }
}
