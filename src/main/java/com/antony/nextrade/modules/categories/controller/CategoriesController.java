package com.antony.nextrade.modules.categories.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.antony.nextrade.common.api.ApiResponse;
import com.antony.nextrade.common.pagination.IPaginationResponse;
import com.antony.nextrade.config.security.RequiresPermission;
import com.antony.nextrade.modules.categories.repository.CategoryRepository;
import com.antony.nextrade.modules.categories.usecase.findall.FindAllCategoryProjection;
import com.antony.nextrade.modules.permissions.model.Permission;
import com.antony.nextrade.services.pagination.PaginationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoryRepository categoryRepository;
    private final PaginationService paginationService;

    @GetMapping
    @RequiresPermission(Permission.CATEGORY_READ)
    public ResponseEntity<ApiResponse<IPaginationResponse<FindAllCategoryProjection>>> findAll(Pageable pageable) {
        var response = paginationService.execute(
                categoryRepository,
                pageable,
                FindAllCategoryProjection.class);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
