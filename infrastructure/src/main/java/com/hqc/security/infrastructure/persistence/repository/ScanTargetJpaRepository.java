package com.hqc.security.infrastructure.persistence.repository;

import com.hqc.security.infrastructure.persistence.entity.ScanTargetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScanTargetJpaRepository extends JpaRepository<ScanTargetJpaEntity, UUID> {
    List<ScanTargetJpaEntity> findAllByScanJobId(UUID scanJobId);
}
