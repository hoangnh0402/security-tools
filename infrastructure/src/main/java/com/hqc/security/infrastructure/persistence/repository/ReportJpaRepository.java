package com.hqc.security.infrastructure.persistence.repository;

import com.hqc.security.infrastructure.persistence.entity.ReportJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportJpaEntity, UUID> {
    List<ReportJpaEntity> findAllByProjectId(UUID projectId);
    List<ReportJpaEntity> findAllByScanJobId(UUID scanJobId);
}
