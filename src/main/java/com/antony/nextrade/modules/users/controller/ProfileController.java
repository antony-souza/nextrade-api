package com.antony.nextrade.modules.users.controller;

import com.antony.nextrade.common.api.ApiResponse;
import com.antony.nextrade.config.security.AuthenticatedUser;
import com.antony.nextrade.config.security.RequiresPermission;
import com.antony.nextrade.modules.permissions.model.Permission;
import com.antony.nextrade.modules.users.usecase.findprofile.FindProfileUseCase;
import com.antony.nextrade.modules.users.usecase.findprofile.ProfileResponse;
import com.antony.nextrade.modules.users.usecase.updateprofile.UpdateProfilePhotosRequest;
import com.antony.nextrade.modules.users.usecase.updateprofile.UpdateProfilePhotosUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final FindProfileUseCase findProfileUseCase;
    private final UpdateProfilePhotosUseCase updateProfilePhotosUseCase;

    @GetMapping
    @RequiresPermission(Permission.PROFILE_READ)
    public ApiResponse<ProfileResponse> findProfile(@AuthenticationPrincipal AuthenticatedUser user) {
        return ApiResponse.success(findProfileUseCase.execute(user.userId()));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiresPermission(Permission.PROFILE_UPDATE)
    public ApiResponse<ProfileResponse> updatePhotos(
            @AuthenticationPrincipal AuthenticatedUser user,
            @ModelAttribute UpdateProfilePhotosRequest request
    ) {
        return ApiResponse.success(updateProfilePhotosUseCase.execute(user.userId(), request));
    }
}
