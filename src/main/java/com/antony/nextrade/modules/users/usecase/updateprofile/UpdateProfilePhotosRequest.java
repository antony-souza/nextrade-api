package com.antony.nextrade.modules.users.usecase.updateprofile;

import org.springframework.web.multipart.MultipartFile;

public record UpdateProfilePhotosRequest(
        MultipartFile avatar,
        MultipartFile cover,
        Boolean removeAvatar,
        Boolean removeCover
) {
}
