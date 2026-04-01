package com.hqc.security.common.domain.port.in;

import com.hqc.security.common.domain.model.CiScanResult;
import java.util.UUID;

public interface CiIntegrationService {
    CiScanResult triggerScanAndWait(UUID projectId, UUID initiatorId, String targetUrl);
}
