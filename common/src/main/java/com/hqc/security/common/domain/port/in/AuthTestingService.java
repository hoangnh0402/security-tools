package com.hqc.security.common.domain.port.in;

import com.hqc.security.common.domain.model.Vulnerability;

import java.util.List;
import java.util.UUID;

/**
 * Use Case: Kiểm tra các lỗi xác thực phổ biến (Authentication & Authorization).
 */
public interface AuthTestingService {
    List<Vulnerability> testAuthentication(UUID projectId, UUID scanJobId, String loginUrl, String targetUrl);
}
