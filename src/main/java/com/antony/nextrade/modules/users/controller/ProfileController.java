package com.antony.nextrade.modules.users.controller;

import com.antony.nextrade.common.api.ApiResponse;
import com.antony.nextrade.config.security.AuthenticatedUser;
import com.antony.nextrade.config.security.RequiresPermission;
import com.antony.nextrade.modules.permissions.model.Permission;
import com.antony.nextrade.modules.users.usecase.findprofile.FindProfileUseCase;
import com.antony.nextrade.modules.users.usecase.findprofile.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final FindProfileUseCase findProfileUseCase;

    @GetMapping
    @RequiresPermission(Permission.PROFILE_READ)
    public ApiResponse<ProfileResponse> findProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.success(findProfileUseCase.execute(user.userId()));
    }
}
