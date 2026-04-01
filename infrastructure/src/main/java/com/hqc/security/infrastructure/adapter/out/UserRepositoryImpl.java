package com.hqc.security.infrastructure.adapter.out;

import com.hqc.security.common.domain.model.Role;
import com.hqc.security.common.domain.model.User;
import com.hqc.security.common.domain.port.out.UserRepository;
import com.hqc.security.infrastructure.persistence.entity.UserJpaEntity;
import com.hqc.security.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserJpaEntity entity = mapToJpaEntity(user);
        UserJpaEntity saved = jpaRepository.save(entity);
        return mapToDomainEntity(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(this::mapToDomainEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::mapToDomainEntity);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username).map(this::mapToDomainEntity);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    // Manual Object Mapper to keep it simple and clean.
    private UserJpaEntity mapToJpaEntity(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.id());
        entity.setUsername(user.username());
        entity.setEmail(user.email());
        entity.setPasswordHash(user.passwordHash());
        entity.setRole(user.role().name());
        entity.setCreatedAt(user.createdAt());
        return entity;
    }

    private User mapToDomainEntity(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                Role.valueOf(entity.getRole()),
                entity.getCreatedAt()
        );
    }
}
