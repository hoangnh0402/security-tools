package com.hqc.security.scan.scanner;

import com.hqc.security.common.domain.model.Vulnerability;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Interface cho Passive Scanner Plugin — tương tự ZAP Passive Scan Rules.
 * Phân tích response headers, cookies, body mà KHÔNG gửi thêm request.
 */
public interface PassiveScanPlugin {

    /** Tên plugin */
    String getName();

    /**
     * Phân tích response đã nhận được.
     * @param url URL đã request
     * @param statusCode HTTP status code
     * @param headers response headers
     * @param body response body
     * @param scanJobId ID scan job
     * @return danh sách vulnerability phát hiện
     */
    List<Vulnerability> analyze(String url, int statusCode, Map<String, List<String>> headers, String body, UUID scanJobId);
}
