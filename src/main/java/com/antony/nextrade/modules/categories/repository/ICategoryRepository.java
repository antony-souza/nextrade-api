package com.antony.nextrade.modules.categories.repository;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.antony.nextrade.common.repositories.IBaseRepository;
import com.antony.nextrade.modules.categories.model.CategoryEntity;

@Repository
public interface ICategoryRepository extends IBaseRepository<CategoryEntity, UUID> {
    boolean existsByCodeAndDeletedAtIsNull(String code);
}
