package com.hqc.security.infrastructure.adapter.out;

import com.hqc.security.common.domain.model.Project;
import com.hqc.security.common.domain.port.out.ProjectRepository;
import com.hqc.security.infrastructure.persistence.entity.ProjectJpaEntity;
import com.hqc.security.infrastructure.persistence.repository.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectJpaRepository jpaRepository;

    @Override
    public Project save(Project project) {
        ProjectJpaEntity entity = mapToJpaEntity(project);
        ProjectJpaEntity saved = jpaRepository.save(entity);
        return mapToDomainEntity(saved);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaRepository.findById(id).map(this::mapToDomainEntity);
    }

    @Override
    public List<Project> findAllByCreatedBy(UUID userId) {
        return jpaRepository.findAllByCreatedBy(userId).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    private ProjectJpaEntity mapToJpaEntity(Project project) {
        ProjectJpaEntity entity = new ProjectJpaEntity();
        entity.setId(project.id());
        entity.setName(project.name());
        entity.setDescription(project.description());
        entity.setCreatedBy(project.createdBy());
        entity.setCreatedAt(project.createdAt());
        return entity;
    }

    private Project mapToDomainEntity(ProjectJpaEntity entity) {
        return new Project(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedBy(),
                entity.getCreatedAt()
        );
    }
}
