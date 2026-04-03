package com.hqc.security.common.domain.service;

import com.hqc.security.common.domain.model.Dependency;
import com.hqc.security.common.domain.model.DependencyVulnerability;
import com.hqc.security.common.domain.port.in.DependencyScannerService;
import com.hqc.security.common.domain.port.out.CveApiClient;
import com.hqc.security.common.domain.port.out.DependencyRepository;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Named
public class DependencyScannerServiceImpl implements DependencyScannerService {

    private static final Logger log = LoggerFactory.getLogger(DependencyScannerServiceImpl.class);

    private final DependencyRepository dependencyRepository;
    private final CveApiClient cveApiClient;

    @Inject
    public DependencyScannerServiceImpl(DependencyRepository dependencyRepository, CveApiClient cveApiClient) {
        this.dependencyRepository = dependencyRepository;
        this.cveApiClient = cveApiClient;
    }

    @Override
    public List<DependencyVulnerability> scanDependencies(UUID projectId, List<Dependency> inputDependencies) {
        List<DependencyVulnerability> allVulns = new ArrayList<>();
        log.info("📦 Starting Dependency Scan for {} items", inputDependencies.size());

        for (Dependency input : inputDependencies) {
            // 1. Lưu thông tin thư viện (Inventory/BOM) gửi lên vào CSDL
            Dependency savedDep = dependencyRepository.saveDependency(
                new Dependency(null, projectId, input.name(), input.version(), input.ecosystem(), LocalDateTime.now())
            );

            // 2. Tương tác với hệ thống OSV.dev để lấy lỗ hổng
            List<DependencyVulnerability> vulns = scanDependencyForCVEs(savedDep);
            if (!vulns.isEmpty()) {
                log.warn("  ⚠️ Found {} vulnerabilities in {}@{}", vulns.size(), savedDep.name(), savedDep.version());
                dependencyRepository.saveVulnerabilities(vulns);
                allVulns.addAll(vulns);
            } else {
                log.debug("  ✅ Clean: {}@{}", savedDep.name(), savedDep.version());
            }
        }

        log.info("✅ Dependency Scan complete — {} total vulnerabilities found", allVulns.size());
        return allVulns;
    }

    @Override
    public List<DependencyVulnerability> scanManifest(UUID projectId, String fileName, String fileContent) {
        List<Dependency> extractedDeps = new ArrayList<>();
        log.info("📄 Parsing manifest file: {}", fileName);

        if (fileName.toLowerCase().endsWith("pom.xml")) {
            extractedDeps.addAll(parsePomXml(fileContent, projectId));
        } else if (fileName.toLowerCase().endsWith("package.json")) {
            extractedDeps.addAll(parsePackageJson(fileContent, projectId));
        } else {
            log.warn("❌ Unsupported manifest file type: {}", fileName);
            throw new IllegalArgumentException("Unsupported manifest file type: " + fileName);
        }

        if (extractedDeps.isEmpty()) {
            log.warn("⚠️ No dependencies found in {}", fileName);
            return new ArrayList<>();
        }

        return scanDependencies(projectId, extractedDeps);
    }

    private List<Dependency> parsePomXml(String content, UUID projectId) {
        List<Dependency> deps = new ArrayList<>();
        // Note: MVP Simple XML parsing using Regex
        Pattern depPattern = Pattern.compile("<dependency>\\s*<groupId>([^<]+)</groupId>\\s*<artifactId>([^<]+)</artifactId>(?:\\s*<version>([^<]+)</version>)?", Pattern.DOTALL);
        Matcher matcher = depPattern.matcher(content);
        
        while (matcher.find()) {
            String groupId = matcher.group(1).trim();
            String artifactId = matcher.group(2).trim();
            String version = "UNKNOWN";
            if (matcher.groupCount() >= 3 && matcher.group(3) != null) {
                version = matcher.group(3).trim();
                // Bỏ qua version chứa biến ${} cho MVP
                if (version.contains("${")) version = "UNKNOWN"; 
            }
            
            String fullName = groupId + ":" + artifactId;
            deps.add(new Dependency(null, projectId, fullName, version, "Maven", LocalDateTime.now()));
        }
        return deps;
    }

    private List<Dependency> parsePackageJson(String content, UUID projectId) {
        List<Dependency> deps = new ArrayList<>();
        // Regex parse "name": "version" in dependencies block
        Pattern blockPattern = Pattern.compile("\"(?:devD|d)ependencies\"\\s*:\\s*\\{([^}]+)\\}", Pattern.DOTALL);
        Matcher blockMatcher = blockPattern.matcher(content);
        
        while (blockMatcher.find()) {
            String block = blockMatcher.group(1);
            Pattern itemPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"");
            Matcher itemMatcher = itemPattern.matcher(block);
            while (itemMatcher.find()) {
                String name = itemMatcher.group(1).trim();
                String version = itemMatcher.group(2).trim().replace("^", "").replace("~", "");
                deps.add(new Dependency(null, projectId, name, version, "npm", LocalDateTime.now()));
            }
        }
        return deps;
    }

    private List<DependencyVulnerability> scanDependencyForCVEs(Dependency dep) {
        // Query the OSV.dev external API via port
        List<DependencyVulnerability> results = new ArrayList<>(cveApiClient.queryVulnerabilities(dep));
        
        // Fallback for hardcoded critical MVP checks if API fails
        if (results.isEmpty()) {
            if ("log4j".equalsIgnoreCase(dep.name()) || "org.apache.logging.log4j:log4j-core".equalsIgnoreCase(dep.name())) {
                if (dep.version() != null && dep.version().startsWith("2.14")) {
                    results.add(new DependencyVulnerability(null, dep.id(), "CVE-2021-44228", "CRITICAL",
                            "Log4Shell — RCE via JNDI lookup.", "2.17.1", LocalDateTime.now()));
                }
            }
        }
        return results;
    }
}
