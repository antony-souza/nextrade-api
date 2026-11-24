package com.antony.nextrade.modules.users.usecase.findprofile;

import com.antony.nextrade.modules.users.model.UserEntity;

import java.util.UUID;

public record ProfileResponse(
        UUID id, String name, String email, String username, UUID roleId, String roleCode,
        String avatarUrl, String coverUrl
) {
    public static ProfileResponse from(UserEntity user) {
        var role = user.getRole();
        return new ProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getUsername(),
                role.getId(), role.getCode(), user.getAvatarUrl(), user.getCoverUrl());
    }
}
