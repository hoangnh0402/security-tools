package com.hqc.security.infrastructure.persistence.repository;

import com.hqc.security.infrastructure.persistence.entity.EndpointJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EndpointJpaRepository extends JpaRepository<EndpointJpaEntity, UUID> {
    List<EndpointJpaEntity> findAllByProjectId(UUID projectId);
}
