package com.antony.nextrade.modules.users.repository;

import com.antony.nextrade.common.repositories.IBaseRepository;
import com.antony.nextrade.config.security.AuthenticatedUser;
import com.antony.nextrade.modules.users.model.UserEntity;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends IBaseRepository<UserEntity, UUID> {
    Optional<UserEntity> findByIdAndDeletedAtIsNull(UUID id);

    Optional<UserEntity> findByEmailAndDeletedAtIsNullAndRole_DeletedAtIsNull(String email);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("""
            select new com.antony.nextrade.config.security.AuthenticatedUser(u.id, r.id, r.code)
            from UserEntity u join u.role r
            where u.id = :id and u.deletedAt is null and r.deletedAt is null
            """)
    Optional<AuthenticatedUser> findAuthenticatedUser(UUID id);
}
