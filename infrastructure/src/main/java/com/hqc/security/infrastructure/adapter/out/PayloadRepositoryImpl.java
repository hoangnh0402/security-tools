package com.hqc.security.infrastructure.adapter.out;

import com.hqc.security.common.domain.model.Payload;
import com.hqc.security.common.domain.port.out.PayloadRepository;
import com.hqc.security.infrastructure.persistence.entity.PayloadJpaEntity;
import com.hqc.security.infrastructure.persistence.repository.PayloadJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PayloadRepositoryImpl implements PayloadRepository {

    private final PayloadJpaRepository jpaRepository;

    @Override
    public List<Payload> findAllActive() {
        return jpaRepository.findByIsActiveTrue().stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payload> findByType(String type) {
        return jpaRepository.findByType(type).stream()
                .map(this::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    private Payload mapToDomainEntity(PayloadJpaEntity entity) {
        return new Payload(
                entity.getId(),
                entity.getType(),
                entity.getValue(),
                entity.isActive()
        );
    }
}
