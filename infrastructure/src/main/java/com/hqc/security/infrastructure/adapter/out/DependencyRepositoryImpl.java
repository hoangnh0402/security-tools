package com.hqc.security.infrastructure.adapter.out;

import com.hqc.security.common.domain.model.Dependency;
import com.hqc.security.common.domain.model.DependencyVulnerability;
import com.hqc.security.common.domain.port.out.DependencyRepository;
import com.hqc.security.infrastructure.persistence.entity.DependencyJpaEntity;
import com.hqc.security.infrastructure.persistence.entity.DependencyVulnerabilityJpaEntity;
import com.hqc.security.infrastructure.persistence.repository.DependencyJpaRepository;
import com.hqc.security.infrastructure.persistence.repository.DependencyVulnerabilityJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DependencyRepositoryImpl implements DependencyRepository {

    private final DependencyJpaRepository dependencyRepo;
    private final DependencyVulnerabilityJpaRepository vulnRepo;

    @Override
    public Dependency saveDependency(Dependency dependency) {
        DependencyJpaEntity entity = mapToJpaEntity(dependency);
        return mapToDomainEntity(dependencyRepo.save(entity));
    }

    @Override
    public void saveVulnerabilities(List<DependencyVulnerability> vulnerabilities) {
        List<DependencyVulnerabilityJpaEntity> entities = vulnerabilities.stream()
                .map(this::mapVulnToJpaEntity)
                .collect(Collectors.toList());
        vulnRepo.saveAll(entities);
    }

    @Override
    public List<Dependency> findByProjectId(UUID projectId) {
        return dependencyRepo.findAllByProjectId(projectId).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<DependencyVulnerability> findVulnsByDependencyId(UUID dependencyId) {
        return vulnRepo.findAllByDependencyId(dependencyId).stream()
                .map(this::mapVulnToDomainEntity)
                .collect(Collectors.toList());
    }

    private Dependency mapToDomainEntity(DependencyJpaEntity entity) {
        return new Dependency(entity.getId(), entity.getProjectId(), entity.getName(), entity.getVersion(), entity.getEcosystem(), entity.getCreatedAt());
    }

    private DependencyJpaEntity mapToJpaEntity(Dependency dom) {
        DependencyJpaEntity entity = new DependencyJpaEntity();
        entity.setId(dom.id());
        entity.setProjectId(dom.projectId());
        entity.setName(dom.name());
        entity.setVersion(dom.version());
        entity.setEcosystem(dom.ecosystem());
        entity.setCreatedAt(dom.createdAt());
        return entity;
    }

    private DependencyVulnerability mapVulnToDomainEntity(DependencyVulnerabilityJpaEntity entity) {
        return new DependencyVulnerability(entity.getId(), entity.getDependencyId(), entity.getCveId(), entity.getSeverity(), entity.getDescription(), entity.getFixedVersion(), entity.getCreatedAt());
    }

    private DependencyVulnerabilityJpaEntity mapVulnToJpaEntity(DependencyVulnerability dom) {
        DependencyVulnerabilityJpaEntity entity = new DependencyVulnerabilityJpaEntity();
        entity.setId(dom.id());
        entity.setDependencyId(dom.dependencyId());
        entity.setCveId(dom.cveId());
        entity.setSeverity(dom.severity());
        entity.setDescription(dom.description());
        entity.setFixedVersion(dom.fixedVersion());
        entity.setCreatedAt(dom.createdAt());
        return entity;
    }
}
