package com.antony.nextrade.modules.users.controller;

import com.antony.nextrade.common.api.ApiResponse;
import com.antony.nextrade.common.pagination.IPaginationResponse;
import com.antony.nextrade.config.security.RequiresPermission;
import com.antony.nextrade.modules.permissions.model.Permission;
import com.antony.nextrade.modules.users.repository.IUserRepository;
import com.antony.nextrade.modules.users.usecase.findall.FindAllUsersProjection;
import com.antony.nextrade.modules.users.usecase.updaterole.UpdateUserRoleRequest;
import com.antony.nextrade.modules.users.usecase.updaterole.UpdateUserRoleUseCase;
import com.antony.nextrade.services.pagination.PaginationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserRepository userRepository;
    private final PaginationService paginationService;
    private final UpdateUserRoleUseCase updateUserRoleUseCase;

    @GetMapping
    @RequiresPermission(Permission.USER_READ)
    public ApiResponse<IPaginationResponse<FindAllUsersProjection>> findAll(Pageable pageable) {
        return ApiResponse.success(paginationService.execute(userRepository, pageable, FindAllUsersProjection.class));
    }

    @PutMapping("/{id}/role")
    @RequiresPermission(Permission.USER_UPDATE)
    @PreAuthorize("principal.roleCode() == T(com.antony.nextrade.modules.roles.model.RoleCodes).ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRole(@PathVariable UUID id, @Valid @RequestBody UpdateUserRoleRequest request) {
        updateUserRoleUseCase.execute(id, request.roleId());
    }
}
