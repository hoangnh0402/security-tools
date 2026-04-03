package com.hqc.security.common.domain.port.in;

import com.hqc.security.common.domain.model.Vulnerability;
import java.util.List;
import java.util.UUID;

/**
 * Use Case: Quét bảo mật API endpoints.
 * Kiểm tra mising auth, rate limit, data exposure trên các endpoint đã crawl.
 */
public interface ApiSecurityService {
    List<Vulnerability> testApiSecurity(UUID projectId, UUID scanJobId, List<String> endpointUrls);
}
