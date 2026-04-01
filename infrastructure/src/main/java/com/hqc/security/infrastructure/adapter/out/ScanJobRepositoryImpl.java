package com.hqc.security.infrastructure.adapter.out;

import com.hqc.security.common.domain.model.ScanJob;
import com.hqc.security.common.domain.model.ScanJobStatus;
import com.hqc.security.common.domain.port.out.ScanJobRepository;
import com.hqc.security.infrastructure.persistence.entity.ScanJobJpaEntity;
import com.hqc.security.infrastructure.persistence.repository.ScanJobJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScanJobRepositoryImpl implements ScanJobRepository {

    private final ScanJobJpaRepository jpaRepository;

    @Override
    public ScanJob save(ScanJob scanJob) {
        ScanJobJpaEntity entity = mapToJpaEntity(scanJob);
        return mapToDomainEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<ScanJob> findById(UUID id) {
        return jpaRepository.findById(id).map(this::mapToDomainEntity);
    }

    @Override
    public List<ScanJob> findAllByProjectId(UUID projectId) {
        return jpaRepository.findAllByProjectId(projectId).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(UUID id, ScanJobStatus status) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.setStatus(status.name());
            if (status == ScanJobStatus.RUNNING) {
                entity.setStartedAt(java.time.LocalDateTime.now());
            } else if (status == ScanJobStatus.DONE || status == ScanJobStatus.FAILED) {
                entity.setFinishedAt(java.time.LocalDateTime.now());
            }
            jpaRepository.save(entity);
        });
    }

    private ScanJob mapToDomainEntity(ScanJobJpaEntity entity) {
        return new ScanJob(
                entity.getId(),
                entity.getProjectId(),
                entity.getCreatedBy(),
                entity.getScanType(),
                ScanJobStatus.valueOf(entity.getStatus()),
                entity.getStartedAt(),
                entity.getFinishedAt()
        );
    }

    private ScanJobJpaEntity mapToJpaEntity(ScanJob domain) {
        ScanJobJpaEntity entity = new ScanJobJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setCreatedBy(domain.createdBy());
        entity.setScanType(domain.scanType());
        entity.setStatus(domain.status().name());
        entity.setStartedAt(domain.startedAt());
        entity.setFinishedAt(domain.finishedAt());
        return entity;
    }
}
