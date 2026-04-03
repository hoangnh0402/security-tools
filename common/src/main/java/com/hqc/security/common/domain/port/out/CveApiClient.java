package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.Dependency;
import com.hqc.security.common.domain.model.DependencyVulnerability;

import java.util.List;

/**
 * Port kết nối tới các dịch vụ tra cứu CVE (OSV.dev, NVD, Snyk).
 */
public interface CveApiClient {
    /**
     * Truy vấn thông tin các lỗ hổng của thư viện.
     * @param dependency thông tin dependency cần kiểm tra
     * @return Danh sách các lỗ hổng (nếu có)
     */
    List<DependencyVulnerability> queryVulnerabilities(Dependency dependency);
}
