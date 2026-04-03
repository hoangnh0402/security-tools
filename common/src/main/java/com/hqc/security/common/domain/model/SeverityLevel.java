package com.hqc.security.common.domain.model;

/**
 * Mức độ nghiêm trọng của lỗ hổng bảo mật.
 * Tương thích với CVSS severity ratings.
 */
public enum SeverityLevel {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    INFO
}
