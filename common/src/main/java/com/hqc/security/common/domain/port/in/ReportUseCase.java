package com.hqc.security.common.domain.port.in;

import com.hqc.security.common.domain.model.ReportSummary;
import java.util.UUID;

public interface ReportUseCase {
    ReportSummary getProjectSummary(UUID projectId);
}
