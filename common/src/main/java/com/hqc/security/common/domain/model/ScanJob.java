package com.hqc.security.common.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScanJob(
    UUID id,
    UUID projectId,
    UUID createdBy,
    String scanType,
    ScanJobStatus status,
    LocalDateTime startedAt,
    LocalDateTime finishedAt
) {
    public ScanJob {
        if (projectId == null) {
            throw new IllegalArgumentException("Scan Job must belong to a project");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("Scan Job must have an initiator (createdBy)");
        }
    }

    // Tiện ích để Clone Object khi thay đổi trạng thái theo chuẩn Immutable Record
    public ScanJob withStatus(ScanJobStatus newStatus) {
        LocalDateTime newStartedAt = (newStatus == ScanJobStatus.RUNNING && this.startedAt == null) 
                                     ? LocalDateTime.now() : this.startedAt;
        LocalDateTime newFinishedAt = (newStatus == ScanJobStatus.DONE || newStatus == ScanJobStatus.FAILED) 
                                     ? LocalDateTime.now() : this.finishedAt;
        return new ScanJob(id, projectId, createdBy, scanType, newStatus, newStartedAt, newFinishedAt);
    }
}
