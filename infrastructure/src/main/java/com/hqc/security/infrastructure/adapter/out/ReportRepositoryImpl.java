package com.hqc.security.infrastructure.adapter.out;

import com.hqc.security.common.domain.model.Report;
import com.hqc.security.common.domain.port.out.ReportRepository;
import com.hqc.security.infrastructure.persistence.entity.ReportJpaEntity;
import com.hqc.security.infrastructure.persistence.repository.ReportJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportJpaRepository jpaRepository;

    @Override
    public Report save(Report report) {
        ReportJpaEntity entity = mapToJpaEntity(report);
        return mapToDomainEntity(jpaRepository.save(entity));
    }

    @Override
    public Optional<Report> findById(UUID id) {
        return jpaRepository.findById(id).map(this::mapToDomainEntity);
    }

    @Override
    public List<Report> findByProjectId(UUID projectId) {
        return jpaRepository.findAllByProjectId(projectId).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByScanJobId(UUID scanJobId) {
        return jpaRepository.findAllByScanJobId(scanJobId).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    private Report mapToDomainEntity(ReportJpaEntity entity) {
        return new Report(
                entity.getId(),
                entity.getProjectId(),
                entity.getScanJobId(),
                entity.getSummary(),
                entity.getFilePath(),
                entity.getCreatedAt()
        );
    }

    private ReportJpaEntity mapToJpaEntity(Report domain) {
        ReportJpaEntity entity = new ReportJpaEntity();
        entity.setId(domain.id());
        entity.setProjectId(domain.projectId());
        entity.setScanJobId(domain.scanJobId());
        entity.setSummary(domain.summary());
        entity.setFilePath(domain.filePath());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
