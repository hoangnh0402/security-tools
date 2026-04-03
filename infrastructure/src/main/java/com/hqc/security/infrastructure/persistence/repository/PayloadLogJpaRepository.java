package com.hqc.security.infrastructure.persistence.repository;

import com.hqc.security.infrastructure.persistence.entity.PayloadLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayloadLogJpaRepository extends JpaRepository<PayloadLogJpaEntity, UUID> {
    List<PayloadLogJpaEntity> findAllByVulnerabilityId(UUID vulnerabilityId);
}
