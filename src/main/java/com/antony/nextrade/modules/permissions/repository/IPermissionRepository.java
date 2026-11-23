package com.antony.nextrade.modules.permissions.repository;

import java.util.Optional;

import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.antony.nextrade.common.repositories.IBaseRepository;
import com.antony.nextrade.modules.permissions.model.PermissionEntity;

@Repository
public interface IPermissionRepository extends IBaseRepository<PermissionEntity, UUID> {




    boolean existsByCodeAndDeletedAtIsNull(String code);


    Optional<PermissionEntity> findByIdAndDeletedAtIsNull(UUID id);
}
