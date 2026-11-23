package com.antony.nextrade.modules.users.usecase.findall;

import java.util.UUID;

public interface FindAllUsersProjection {
    UUID getId();

    String getName();

    String getEmail();

    String getUsername();

    String getRoleCode();
}
