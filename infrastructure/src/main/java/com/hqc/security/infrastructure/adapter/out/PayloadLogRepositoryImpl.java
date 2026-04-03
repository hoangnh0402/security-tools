package com.hqc.security.infrastructure.adapter.out;

import com.hqc.security.common.domain.model.PayloadLog;
import com.hqc.security.common.domain.port.out.PayloadLogRepository;
import com.hqc.security.infrastructure.persistence.entity.PayloadLogJpaEntity;
import com.hqc.security.infrastructure.persistence.repository.PayloadLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PayloadLogRepositoryImpl implements PayloadLogRepository {

    private final PayloadLogJpaRepository jpaRepository;

    @Override
    public PayloadLog save(PayloadLog payloadLog) {
        PayloadLogJpaEntity entity = mapToJpaEntity(payloadLog);
        return mapToDomainEntity(jpaRepository.save(entity));
    }

    @Override
    public List<PayloadLog> findByVulnerabilityId(UUID vulnerabilityId) {
        return jpaRepository.findAllByVulnerabilityId(vulnerabilityId).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    private PayloadLog mapToDomainEntity(PayloadLogJpaEntity entity) {
        return new PayloadLog(
                entity.getId(),
                entity.getVulnerabilityId(),
                entity.getPayload(),
                entity.getRequestData(),
                entity.getResponseData(),
                entity.getResponseTime(),
                entity.getCreatedAt()
        );
    }

    private PayloadLogJpaEntity mapToJpaEntity(PayloadLog domain) {
        PayloadLogJpaEntity entity = new PayloadLogJpaEntity();
        entity.setId(domain.id());
        entity.setVulnerabilityId(domain.vulnerabilityId());
        entity.setPayload(domain.payload());
        entity.setRequestData(domain.requestData());
        entity.setResponseData(domain.responseData());
        entity.setResponseTime(domain.responseTime());
        entity.setCreatedAt(domain.createdAt());
        return entity;
    }
}
