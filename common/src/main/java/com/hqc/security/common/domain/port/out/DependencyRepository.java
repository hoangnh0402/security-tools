package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.Dependency;
import com.hqc.security.common.domain.model.DependencyVulnerability;

import java.util.List;
import java.util.UUID;

public interface DependencyRepository {
    Dependency saveDependency(Dependency dependency);
    void saveVulnerabilities(List<DependencyVulnerability> vulnerabilities);
    List<Dependency> findByProjectId(UUID projectId);
    List<DependencyVulnerability> findVulnsByDependencyId(UUID dependencyId);
}
