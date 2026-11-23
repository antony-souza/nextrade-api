package com.antony.nextrade.modules.rolepermissions.repository;

import com.antony.nextrade.common.repositories.IBaseRepository;
import com.antony.nextrade.modules.rolepermissions.model.RolePermissionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface IRolePermissionRepository extends IBaseRepository<RolePermissionEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RolePermissionEntity> findByRoleIdAndPermissionId(UUID roleId, UUID permissionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RolePermissionEntity> findByIdAndDeletedAtIsNull(UUID id);

    @Transactional(readOnly = true)
    boolean existsByRole_IdAndPermission_CodeAndDeletedAtIsNullAndRole_DeletedAtIsNullAndPermission_DeletedAtIsNull(
            UUID roleId, String permissionCode);
}
