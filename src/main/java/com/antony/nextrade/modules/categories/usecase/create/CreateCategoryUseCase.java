package com.antony.nextrade.modules.categories.usecase.create;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.antony.nextrade.modules.categories.model.CategoryEntity;
import com.antony.nextrade.modules.categories.repository.ICategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {
    private final ICategoryRepository categoryRepository;

    @Transactional
    public CreateCategoryResponse execute(CreateCategoyRequest createCategoryRequest) {
        if (categoryRepository.existsByCodeAndDeletedAtIsNull(createCategoryRequest.code())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Category already exists");
        }

        var category = new CategoryEntity();

        category.setName(createCategoryRequest.name().trim());
        category.setCode(createCategoryRequest.code().trim());

        if (createCategoryRequest.description() != null) {
            category.setDescription(createCategoryRequest.description().trim());
        }

        categoryRepository.save(category);

        return new CreateCategoryResponse("Category created successfully");
    }
}
