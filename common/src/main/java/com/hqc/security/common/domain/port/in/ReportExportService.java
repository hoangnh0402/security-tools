package com.hqc.security.common.domain.port.in;

import java.util.UUID;

public interface ReportExportService {
    byte[] exportScanJobCsv(UUID scanJobId);
}
