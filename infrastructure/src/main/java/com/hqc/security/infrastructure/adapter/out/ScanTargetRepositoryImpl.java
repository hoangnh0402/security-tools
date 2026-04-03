package com.hqc.security.infrastructure.adapter.out;

import com.hqc.security.common.domain.model.ScanTarget;
import com.hqc.security.common.domain.port.out.ScanTargetRepository;
import com.hqc.security.infrastructure.persistence.entity.ScanTargetJpaEntity;
import com.hqc.security.infrastructure.persistence.repository.ScanTargetJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScanTargetRepositoryImpl implements ScanTargetRepository {

    private final ScanTargetJpaRepository jpaRepository;

    @Override
    public ScanTarget save(ScanTarget scanTarget) {
        ScanTargetJpaEntity entity = mapToJpaEntity(scanTarget);
        return mapToDomainEntity(jpaRepository.save(entity));
    }

    @Override
    public List<ScanTarget> findByScanJobId(UUID scanJobId) {
        return jpaRepository.findAllByScanJobId(scanJobId).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    private ScanTarget mapToDomainEntity(ScanTargetJpaEntity entity) {
        return new ScanTarget(
                entity.getId(),
                entity.getScanJobId(),
                entity.getTargetUrl(),
                entity.getMethod(),
                entity.getHeaders(),
                entity.getBody()
        );
    }

    private ScanTargetJpaEntity mapToJpaEntity(ScanTarget domain) {
        ScanTargetJpaEntity entity = new ScanTargetJpaEntity();
        entity.setId(domain.id());
        entity.setScanJobId(domain.scanJobId());
        entity.setTargetUrl(domain.targetUrl());
        entity.setMethod(domain.method());
        entity.setHeaders(domain.headers());
        entity.setBody(domain.body());
        return entity;
    }
}
