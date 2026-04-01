package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.Endpoint;

import java.util.List;
import java.util.UUID;

public interface EndpointRepository {
    Endpoint save(Endpoint endpoint);
    void saveAll(List<Endpoint> endpoints);
    List<Endpoint> findAllByProjectId(UUID projectId);
}
