package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.PayloadLog;
import java.util.List;
import java.util.UUID;

public interface PayloadLogRepository {
    PayloadLog save(PayloadLog payloadLog);
    List<PayloadLog> findByVulnerabilityId(UUID vulnerabilityId);
}
