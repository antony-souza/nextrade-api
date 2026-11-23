package com.antony.nextrade.modules.roles.usecase.findall;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

@JsonPropertyOrder({"id", "name", "code", "level"})
public interface FindAllRolesProjection {
    UUID getId();

    String getName();

    String getCode();

    Integer getLevel();
}
