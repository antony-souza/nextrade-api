package com.antony.nextrade.modules.users.usecase.findprofile;

import java.util.UUID;

public record ProfileResponse(UUID id, String name, String email, String username, UUID roleId, String roleCode) {
}
