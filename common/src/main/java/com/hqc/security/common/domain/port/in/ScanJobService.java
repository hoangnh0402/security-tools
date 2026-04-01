package com.hqc.security.common.domain.port.in;

import com.hqc.security.common.domain.model.ScanJob;
import java.util.UUID;

public interface ScanJobService {
    ScanJob createJob(UUID projectId, UUID initiatorId, String scanType);
    void executeJob(UUID scanJobId, String targetUrl);
    ScanJob getScanStatus(UUID scanJobId);
}
