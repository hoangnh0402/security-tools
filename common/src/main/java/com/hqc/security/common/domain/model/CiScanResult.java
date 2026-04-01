package com.hqc.security.common.domain.model;

public record CiScanResult(
    boolean success, 
    String message,
    int highVulns,
    int mediumVulns,
    int lowVulns
) {}
