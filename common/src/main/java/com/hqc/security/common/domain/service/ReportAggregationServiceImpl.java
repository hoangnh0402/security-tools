package com.hqc.security.common.domain.service;

import com.hqc.security.common.domain.model.*;
import com.hqc.security.common.domain.port.in.ReportUseCase;
import com.hqc.security.common.domain.port.out.DependencyRepository;
import com.hqc.security.common.domain.port.out.ScanJobRepository;
import com.hqc.security.common.domain.port.out.VulnerabilityRepository;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;
import java.util.UUID;

@Named
public class ReportAggregationServiceImpl implements ReportUseCase {

    private final ScanJobRepository scanJobRepository;
    private final VulnerabilityRepository vulnerabilityRepository;
    private final DependencyRepository dependencyRepository;

    @Inject
    public ReportAggregationServiceImpl(
            ScanJobRepository scanJobRepository, 
            VulnerabilityRepository vulnerabilityRepository, 
            DependencyRepository dependencyRepository) {
        this.scanJobRepository = scanJobRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.dependencyRepository = dependencyRepository;
    }

    @Override
    public ReportSummary getProjectSummary(UUID projectId) {
        // 1. Phân tích DAST
        List<ScanJob> jobs = scanJobRepository.findByProjectId(projectId);
        int totalScanJobs = jobs.size();
        
        int high = 0;
        int medium = 0;
        int low = 0;

        for (ScanJob job : jobs) {
            List<Vulnerability> vulns = vulnerabilityRepository.findAllByScanJobId(job.id());
            for (Vulnerability v : vulns) {
                if ("HIGH".equalsIgnoreCase(v.severity())) high++;
                else if ("MEDIUM".equalsIgnoreCase(v.severity())) medium++;
                else low++;
            }
        }

        // 2. Phân tích SAST (SCA)
        List<Dependency> deps = dependencyRepository.findByProjectId(projectId);
        int totalDependenciesChecked = deps.size();
        
        int depCritical = 0;
        int depHigh = 0;

        for (Dependency dep : deps) {
            List<DependencyVulnerability> depVulns = dependencyRepository.findVulnsByDependencyId(dep.id());
            for (DependencyVulnerability dv : depVulns) {
                if ("CRITICAL".equalsIgnoreCase(dv.severity())) depCritical++;
                else if ("HIGH".equalsIgnoreCase(dv.severity())) depHigh++;
            }
        }

        return new ReportSummary(
                totalScanJobs, totalDependenciesChecked, 
                high, medium, low, 
                depCritical, depHigh
        );
    }
}
