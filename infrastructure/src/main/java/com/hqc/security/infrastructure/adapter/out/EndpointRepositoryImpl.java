package com.hqc.security.infrastructure.adapter.out;

import com.hqc.security.common.domain.model.Endpoint;
import com.hqc.security.common.domain.port.out.EndpointRepository;
import com.hqc.security.infrastructure.persistence.entity.EndpointJpaEntity;
import com.hqc.security.infrastructure.persistence.repository.EndpointJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EndpointRepositoryImpl implements EndpointRepository {

    private final EndpointJpaRepository jpaRepository;

    @Override
    public Endpoint save(Endpoint endpoint) {
        EndpointJpaEntity entity = mapToJpaEntity(endpoint);
        return mapToDomainEntity(jpaRepository.save(entity));
    }

    @Override
    public void saveAll(List<Endpoint> endpoints) {
        List<EndpointJpaEntity> entities = endpoints.stream()
                .map(this::mapToJpaEntity)
                .collect(Collectors.toList());
        jpaRepository.saveAll(entities);
    }

    @Override
    public List<Endpoint> findAllByProjectId(UUID projectId) {
        return jpaRepository.findAllByProjectId(projectId).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    private Endpoint mapToDomainEntity(EndpointJpaEntity entity) {
        return new Endpoint(
                entity.getId(),
                entity.getProjectId(),
                entity.getUrl(),
                entity.getMethod(),
                entity.getParameters(),
                entity.getDiscoveredAt()
        );
    }

    private EndpointJpaEntity mapToJpaEntity(Endpoint endpoint) {
        EndpointJpaEntity entity = new EndpointJpaEntity();
        entity.setId(endpoint.id());
        entity.setProjectId(endpoint.projectId());
        entity.setUrl(endpoint.url());
        entity.setMethod(endpoint.method());
        entity.setParameters(endpoint.parameters());
        entity.setDiscoveredAt(endpoint.discoveredAt());
        return entity;
    }
}
