package com.antony.nextrade.modules.roles.usecase.update;

import com.antony.nextrade.modules.roles.model.RoleCodes;
import com.antony.nextrade.modules.roles.model.RoleEntity;
import com.antony.nextrade.modules.roles.repository.IRoleRepository;
import com.antony.nextrade.modules.roles.services.RoleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase {
    private final IRoleRepository roleRepository;
    private final RoleValidationService roleValidationService;

    public UpdateRoleResponse execute(UUID roleId, UpdateRoleRequest updateRoleRequest) {
        RoleEntity role = roleRepository.findByIdAndDeletedAtIsNull(roleId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role não encontrada"
                ));

        String code = updateRoleRequest.code().strip().toLowerCase(Locale.ROOT);
        if ((RoleCodes.ADMIN.equals(role.getCode()) || RoleCodes.USER.equals(role.getCode()))
                && !role.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O código de uma role padrão é fixo");
        }
        roleValidationService.validateDuplicateRole(code, roleId);

        role.setName(updateRoleRequest.name().trim());
        role.setCode(code);
        role.setLevel(updateRoleRequest.level());

        roleRepository.save(role);

        return new UpdateRoleResponse("Role atualizada com sucesso");
    }
}
