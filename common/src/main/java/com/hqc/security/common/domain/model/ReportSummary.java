package com.hqc.security.common.domain.model;

public record ReportSummary(
    int totalScanJobs,
    int totalDependenciesChecked,
    int totalVulnsHigh,
    int totalVulnsMedium,
    int totalVulnsLow,
    int totalDepVulnsCritical,
    int totalDepVulnsHigh
) {}
