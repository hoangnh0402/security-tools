package com.hqc.security.infrastructure.persistence.repository;

import com.hqc.security.infrastructure.persistence.entity.ScanJobJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScanJobJpaRepository extends JpaRepository<ScanJobJpaEntity, UUID> {
    List<ScanJobJpaEntity> findAllByProjectId(UUID projectId);
}
