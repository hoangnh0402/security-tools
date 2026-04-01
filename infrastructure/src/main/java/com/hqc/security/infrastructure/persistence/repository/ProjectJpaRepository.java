package com.hqc.security.infrastructure.persistence.repository;

import com.hqc.security.infrastructure.persistence.entity.ProjectJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectJpaRepository extends JpaRepository<ProjectJpaEntity, UUID> {
    List<ProjectJpaEntity> findAllByCreatedBy(UUID createdBy);
}
