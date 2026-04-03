package com.hqc.security.common.domain.port.in;

import com.hqc.security.common.domain.model.Dependency;
import com.hqc.security.common.domain.model.DependencyVulnerability;

import java.util.List;
import java.util.UUID;

public interface DependencyScannerService {
    List<DependencyVulnerability> scanDependencies(UUID projectId, List<Dependency> dependencies);

    /**
     * Phân tích file manifest (pom.xml, package.json) và scan lỗi.
     * @param projectId ID dự án
     * @param fileName Tên file (để suy ra ecosystem)
     * @param fileContent Nội dung file
     * @return Danh sách lỗ hổng
     */
    List<DependencyVulnerability> scanManifest(UUID projectId, String fileName, String fileContent);
}
