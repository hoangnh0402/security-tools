package com.hqc.security.api.dto.response;

import com.hqc.security.common.domain.model.ScanJob;
import com.hqc.security.common.domain.model.ScanJobStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScanJobResponse(
    UUID id,
    UUID projectId,
    String scanType,
    ScanJobStatus status,
    LocalDateTime startedAt,
    LocalDateTime finishedAt
) {
    public static ScanJobResponse fromDomain(ScanJob job) {
        return new ScanJobResponse(
            job.id(),
            job.projectId(),
            job.scanType(),
            job.status(),
            job.startedAt(),
            job.finishedAt()
        );
    }
}
