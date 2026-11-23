package com.antony.nextrade.modules.roles.usecase.create;

import com.antony.nextrade.modules.roles.model.RoleEntity;
import com.antony.nextrade.modules.roles.repository.IRoleRepository;
import com.antony.nextrade.modules.roles.services.RoleValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {
    private final IRoleRepository roleRepository;
    private final RoleValidationService roleValidationService;

    public CreateRoleResponse execute(CreateRoleRequest createRoleRequest) {

        String code = createRoleRequest.code().strip().toLowerCase(Locale.ROOT);
        roleValidationService.validateDuplicateRole(code, null);

        RoleEntity role = new RoleEntity();

        role.setName(createRoleRequest.name().trim());
        role.setCode(code);
        role.setLevel(createRoleRequest.level());

        roleRepository.save(role);

        return new CreateRoleResponse("Role cadastrada com sucesso!");
    }
}
