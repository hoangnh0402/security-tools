package com.hqc.security.infrastructure.persistence.repository;

import com.hqc.security.infrastructure.persistence.entity.PayloadJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PayloadJpaRepository extends JpaRepository<PayloadJpaEntity, UUID> {
    List<PayloadJpaEntity> findByIsActiveTrue();
    List<PayloadJpaEntity> findByType(String type);
}
