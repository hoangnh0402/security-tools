package com.hqc.security.common.domain.service;

import com.hqc.security.common.domain.model.Dependency;
import com.hqc.security.common.domain.model.DependencyVulnerability;
import com.hqc.security.common.domain.port.in.DependencyScannerService;
import com.hqc.security.common.domain.port.out.DependencyRepository;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Named
public class DependencyScannerServiceImpl implements DependencyScannerService {

    private final DependencyRepository dependencyRepository;

    @Inject
    public DependencyScannerServiceImpl(DependencyRepository dependencyRepository) {
        this.dependencyRepository = dependencyRepository;
    }

    @Override
    public List<DependencyVulnerability> scanDependencies(UUID projectId, List<Dependency> inputDependencies) {
        List<DependencyVulnerability> allVulns = new ArrayList<>();

        for (Dependency input : inputDependencies) {
            // 1. Lưu thông tin thư viện (Inventory/BOM) gửi lên vào CSDL Projects
            Dependency savedDep = dependencyRepository.saveDependency(
                new Dependency(null, projectId, input.name(), input.version(), input.ecosystem(), LocalDateTime.now())
            );

            // 2. Logic Scanner tĩnh giả lập cơ chế tra cứu CVE (Common Vulnerabilities and Exposures)
            List<DependencyVulnerability> vulns = scanDependencyForCVEs(savedDep);
            if (!vulns.isEmpty()) {
                // Nếu báo đỏ, lưu vào DB và trả về
                dependencyRepository.saveVulnerabilities(vulns);
                allVulns.addAll(vulns);
            }
        }

        return allVulns;
    }

    private List<DependencyVulnerability> scanDependencyForCVEs(Dependency dep) {
        List<DependencyVulnerability> results = new ArrayList<>();
        // MVP: Giả lập database CVE Server (SCA Scanner tĩnh) nhanh bằng vòng lặp so sánh.
        
        // Cảnh báo 1: Log4Shell huyền thoại
        if ("log4j".equalsIgnoreCase(dep.name()) || "org.apache.logging.log4j:log4j-core".equalsIgnoreCase(dep.name())) {
            // Check version (Kiểm tra version thuộc diện 2.14.x)
            if (dep.version() != null && dep.version().startsWith("2.14")) {
                results.add(new DependencyVulnerability(
                        null,
                        dep.id(),
                        "CVE-2021-44228",
                        "CRITICAL",
                        "Lỗ hổng thực thi lệnh từ xa (RCE) cực kỳ nghiêm trọng (Log4Shell) do tính năng JNDI lookup.",
                        "2.17.1",
                        LocalDateTime.now()
                ));
            }
        }

        // Cảnh báo 2: Deserialization bẩn
        if ("jackson-databind".equalsIgnoreCase(dep.name()) || "com.fasterxml.jackson.core:jackson-databind".equalsIgnoreCase(dep.name())) {
            if (dep.version() != null && dep.version().startsWith("2.9")) {
                results.add(new DependencyVulnerability(
                        null,
                        dep.id(),
                        "CVE-2019-16335",
                        "HIGH",
                        "Lỗi Deserialization trong thư viện phân tích JSON jackson-databind. Rủi ro chạy code tùy ý.",
                        "2.9.10",
                        LocalDateTime.now()
                ));
            }
        }

        // Tương lai: Call API của NVD / GitHub Advisory Database OSV.dev để fetch động.
        return results;
    }
}
