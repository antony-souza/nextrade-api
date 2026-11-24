package com.antony.nextrade.modules.categories.usecase.findall;

import java.util.UUID;

public interface FindAllCategoryProjection {
    UUID getId();

    String getName();

    String getCode();
}
