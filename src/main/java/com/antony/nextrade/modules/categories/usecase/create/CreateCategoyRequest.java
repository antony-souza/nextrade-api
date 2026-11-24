package com.antony.nextrade.modules.categories.usecase.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoyRequest(
    @NotBlank (message = "Category name is required")
    @Size (max = 100, message = "Category name must not exceed 100 characters")
    String name,

    @NotBlank (message = "Category code is required")
    @Size (max = 50, message = "Category code must not exceed 50 characters")
    String code,

    @Size (max = 200, message = "Category description must not exceed 200 characters")
    String description
) {}
