package com.antony.nextrade.modules.rolepermissions.controller;

import com.antony.nextrade.common.api.ApiResponse;
import com.antony.nextrade.common.pagination.IPaginationResponse;
import com.antony.nextrade.config.security.AuthenticatedUser;
import com.antony.nextrade.config.security.RequiresPermission;
import com.antony.nextrade.modules.permissions.model.Permission;
import com.antony.nextrade.modules.rolepermissions.repository.IRolePermissionRepository;
import com.antony.nextrade.modules.rolepermissions.usecase.create.CreateRolePermissionRequest;
import com.antony.nextrade.modules.rolepermissions.usecase.create.CreateRolePermissionResponse;
import com.antony.nextrade.modules.rolepermissions.usecase.create.CreateRolePermissionUseCase;
import com.antony.nextrade.modules.rolepermissions.usecase.delete.DeleteRolePermissionUseCase;
import com.antony.nextrade.modules.rolepermissions.usecase.findall.FindAllRolePermissionsProjection;
import com.antony.nextrade.services.pagination.PaginationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/role-permissions")
@RequiredArgsConstructor
public class RolePermissionsController {
    private final CreateRolePermissionUseCase createRolePermissionUseCase;
    private final DeleteRolePermissionUseCase deleteRolePermissionUseCase;
    private final IRolePermissionRepository rolePermissionRepository;
    private final PaginationService paginationService;

    @GetMapping
    @RequiresPermission(Permission.ROLEPERMISSION_READ)
    public ApiResponse<IPaginationResponse<FindAllRolePermissionsProjection>> findAll(Pageable pageable) {
        return ApiResponse.success(paginationService.execute(
                rolePermissionRepository, pageable, FindAllRolePermissionsProjection.class));
    }

    @PostMapping
    @RequiresPermission(Permission.ROLEPERMISSION_CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateRolePermissionResponse> create(
            @AuthenticationPrincipal AuthenticatedUser actor,
            @Valid @RequestBody CreateRolePermissionRequest request
    ) {
        return ApiResponse.success(createRolePermissionUseCase.execute(actor, request));
    }

    @DeleteMapping("/{id}")
    @RequiresPermission(Permission.ROLEPERMISSION_DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser actor, @PathVariable UUID id) {
        deleteRolePermissionUseCase.execute(actor, id);
    }
}
