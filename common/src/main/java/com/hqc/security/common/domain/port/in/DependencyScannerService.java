package com.hqc.security.common.domain.port.in;

import com.hqc.security.common.domain.model.Dependency;
import com.hqc.security.common.domain.model.DependencyVulnerability;

import java.util.List;
import java.util.UUID;

public interface DependencyScannerService {
    List<DependencyVulnerability> scanDependencies(UUID projectId, List<Dependency> dependencies);
}
