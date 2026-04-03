package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.Report;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRepository {
    Report save(Report report);
    Optional<Report> findById(UUID id);
    List<Report> findByProjectId(UUID projectId);
    List<Report> findByScanJobId(UUID scanJobId);
}
