package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.ScanJob;
import com.hqc.security.common.domain.model.ScanJobStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScanJobRepository {
    ScanJob save(ScanJob scanJob);
    Optional<ScanJob> findById(UUID id);
    List<ScanJob> findAllByProjectId(UUID projectId);
    void updateStatus(UUID id, ScanJobStatus status);
}
