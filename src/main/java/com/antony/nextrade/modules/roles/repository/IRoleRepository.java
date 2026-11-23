package com.antony.nextrade.modules.roles.repository;

import com.antony.nextrade.common.repositories.IBaseRepository;
import com.antony.nextrade.modules.roles.model.RoleEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRoleRepository extends IBaseRepository<RoleEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RoleEntity r where r.code = :code and r.deletedAt is null")
    Optional<RoleEntity> findByCodeForUpdate(String code);

    Optional<RoleEntity> findByCodeAndDeletedAtIsNull(String code);

    Optional<RoleEntity> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByCodeAndDeletedAtIsNull(String code);

    boolean existsByCodeAndIdNotAndDeletedAtIsNull(String code, UUID id);
}
