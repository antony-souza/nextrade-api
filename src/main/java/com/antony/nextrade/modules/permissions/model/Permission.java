package com.antony.nextrade.modules.permissions.model;

public enum Permission {
    PROFILE_READ,
    PROFILE_UPDATE,
    USER_READ,
    USER_UPDATE,
    ROLE_READ,
    ROLE_CREATE,
    ROLE_UPDATE,
    PERMISSION_READ,
    PERMISSION_CREATE,
    ROLEPERMISSION_READ,
    ROLEPERMISSION_CREATE,
    ROLEPERMISSION_DELETE,
    CATEGORY_READ,
    CATEGORY_CREATE,
    CATEGORY_UPDATE,
    CATEGORY_DELETE;

    public String getCode() {
        return name();
    }
}
