package com.antony.nextrade.modules.permissions.usecase.create;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.antony.nextrade.modules.permissions.model.PermissionEntity;
import com.antony.nextrade.modules.permissions.repository.IPermissionRepository;

import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CreatePermissionUseCase {
    private final IPermissionRepository permissionRepository;

    @Transactional
    public CreatePermissionResponse execute(CreatePermissionRequest createPermissionRequest) {
        String code = createPermissionRequest.code().strip().toUpperCase(Locale.ROOT);
        if (permissionRepository.existsByCodeAndDeletedAtIsNull(code)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Já existe uma permissão com este código");
        }

        var permission = new PermissionEntity();

        permission.setName(createPermissionRequest.name().trim());
        permission.setCode(code);
        permission.setDescription(createPermissionRequest.description().trim());

        permissionRepository.save(permission);

        return new CreatePermissionResponse("Permissão cadastrada com sucesso");
    }
}
