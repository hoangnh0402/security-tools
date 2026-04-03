package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.ScanTarget;
import java.util.List;
import java.util.UUID;

public interface ScanTargetRepository {
    ScanTarget save(ScanTarget scanTarget);
    List<ScanTarget> findByScanJobId(UUID scanJobId);
}
