package com.antony.nextrade.modules.categories.usecase.findall;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "id", "name", "code" })
public interface FindAllCategoryProjection {
    UUID getId();

    String getName();

    String getCode();
}
