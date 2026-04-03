package com.hqc.security.common.domain.service;

import com.hqc.security.common.domain.model.Vulnerability;
import com.hqc.security.common.domain.model.VulnerabilityType;
import com.hqc.security.common.domain.port.in.ApiSecurityService;
import com.hqc.security.common.domain.port.out.VulnerabilityRepository;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * API Security Testing Service — tương tự ZAP API Scan.
 * Thực hiện 4 loại kiểm tra: Missing Auth, Rate Limit, Data Exposure, Error Handling.
 */
@Named
public class ApiSecurityServiceImpl implements ApiSecurityService {

    private static final Logger log = LoggerFactory.getLogger(ApiSecurityServiceImpl.class);

    private final VulnerabilityRepository vulnerabilityRepository;
    private final HttpClient httpClient;

    private static final List<Pattern> SENSITIVE_DATA_PATTERNS = List.of(
        Pattern.compile("\"password\"\\s*:\\s*\"[^\"]+\""),
        Pattern.compile("\"secret\"\\s*:\\s*\"[^\"]+\""),
        Pattern.compile("\"token\"\\s*:\\s*\"[a-zA-Z0-9._-]{20,}\""),
        Pattern.compile("\"api[_-]?key\"\\s*:\\s*\"[^\"]+\""),
        Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"),
        Pattern.compile("\\b\\d{4}[- ]?\\d{4}[- ]?\\d{4}[- ]?\\d{4}\\b"),  // Credit card
        Pattern.compile("\"ssn\"\\s*:\\s*\"\\d{3}-\\d{2}-\\d{4}\"")
    );

    @Inject
    public ApiSecurityServiceImpl(VulnerabilityRepository vulnerabilityRepository) {
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    @Override
    public List<Vulnerability> testApiSecurity(UUID projectId, UUID scanJobId, List<String> endpointUrls) {
        List<Vulnerability> allVulns = new ArrayList<>();
        log.info("🔐 Starting API Security Test — {} endpoints", endpointUrls.size());

        for (String url : endpointUrls) {
            try {
                // Test 1: Missing Authentication
                allVulns.addAll(testMissingAuth(url, scanJobId));

                // Test 2: Rate Limit
                allVulns.addAll(testRateLimit(url, scanJobId));

                // Test 3: Data Exposure
                allVulns.addAll(testDataExposure(url, scanJobId));

                // Test 4: Error Handling
                allVulns.addAll(testErrorHandling(url, scanJobId));

            } catch (Exception e) {
                log.debug("  ⏭️ Skip {} — {}", url, e.getMessage());
            }
        }

        if (!allVulns.isEmpty()) {
            vulnerabilityRepository.saveAll(allVulns);
            log.info("💾 Saved {} API security vulnerabilities", allVulns.size());
        }

        return allVulns;
    }

    /**
     * Test 1: Missing Authentication — gọi API endpoint không có token, check 200.
     */
    private List<Vulnerability> testMissingAuth(String url, UUID scanJobId) {
        List<Vulnerability> results = new ArrayList<>();
        try {
            HttpResponse<String> response = sendGet(url);
            if (response != null && response.statusCode() == 200) {
                String body = response.body();
                // Nếu trả về data (JSON array/object) mà không yêu cầu auth → BUG
                if ((body.startsWith("[") || body.startsWith("{")) && body.length() > 20) {
                    results.add(new Vulnerability(null, scanJobId, null,
                        VulnerabilityType.MISSING_AUTH.name(), "HIGH",
                        "API Endpoint thiếu Authentication — " + url,
                        "Endpoint trả về data (HTTP 200) mà không yêu cầu authentication token.",
                        "Kẻ tấn công có thể truy cập API không cần đăng nhập.",
                        "Thêm authentication middleware (JWT/OAuth2). Từ chối request thiếu Authorization header.",
                        "URL: " + url + "\nStatus: 200\nBody length: " + body.length(),
                        "OPEN", LocalDateTime.now()));
                }
            }
        } catch (Exception ignored) {}
        return results;
    }

    /**
     * Test 2: Rate Limit — gửi 30 request liên tục, check có bị 429 không.
     */
    private List<Vulnerability> testRateLimit(String url, UUID scanJobId) {
        List<Vulnerability> results = new ArrayList<>();
        try {
            int successCount = 0;
            for (int i = 0; i < 30; i++) {
                HttpResponse<String> r = sendGet(url);
                if (r != null && r.statusCode() == 200) successCount++;
                if (r != null && r.statusCode() == 429) return results; // Rate limit works
            }
            if (successCount >= 28) { // 93%+ success = no rate limit
                results.add(new Vulnerability(null, scanJobId, null,
                    VulnerabilityType.RATE_LIMIT_BYPASS.name(), "MEDIUM",
                    "API Endpoint thiếu Rate Limiting — " + url,
                    "Gửi 30 request liên tục, " + successCount + " request thành công mà không bị rate limit.",
                    "Kẻ tấn công có thể brute-force hoặc DDoS endpoint.",
                    "Implement rate limiting (ví dụ: 100 req/phút). Sử dụng middleware throttle.",
                    "URL: " + url + "\nSuccess: " + successCount + "/30",
                    "OPEN", LocalDateTime.now()));
            }
        } catch (Exception ignored) {}
        return results;
    }

    /**
     * Test 3: Data Exposure — scan response body chứa sensitive data patterns.
     */
    private List<Vulnerability> testDataExposure(String url, UUID scanJobId) {
        List<Vulnerability> results = new ArrayList<>();
        try {
            HttpResponse<String> response = sendGet(url);
            if (response == null || response.statusCode() != 200) return results;

            String body = response.body();
            for (Pattern pattern : SENSITIVE_DATA_PATTERNS) {
                var matcher = pattern.matcher(body);
                if (matcher.find()) {
                    String match = matcher.group();
                    if (match.length() > 50) match = match.substring(0, 50) + "...";
                    results.add(new Vulnerability(null, scanJobId, null,
                        VulnerabilityType.DATA_EXPOSURE.name(), "HIGH",
                        "Sensitive Data Exposure — " + url,
                        "API response chứa dữ liệu nhạy cảm matching: " + pattern.pattern(),
                        "Dữ liệu cá nhân hoặc credentials bị lộ qua API response.",
                        "Không trả về sensitive fields (password, token, SSN). Sử dụng DTO projection.",
                        "URL: " + url + "\nPattern: " + pattern.pattern() + "\nMatch: " + match,
                        "OPEN", LocalDateTime.now()));
                    break;
                }
            }
        } catch (Exception ignored) {}
        return results;
    }

    /**
     * Test 4: Error Handling — gửi request sai format, check stack trace leak.
     */
    private List<Vulnerability> testErrorHandling(String url, UUID scanJobId) {
        List<Vulnerability> results = new ArrayList<>();
        try {
            // Gửi request với path parameter sai
            String badUrl = url + "/../../../../etc/passwd";
            HttpResponse<String> response = sendGet(badUrl);
            if (response != null) {
                String body = response.body().toLowerCase();
                if (body.contains("stacktrace") || body.contains("exception")
                        || body.contains("at com.") || body.contains("at org.")
                        || body.contains("root:x:0:0")) {
                    results.add(new Vulnerability(null, scanJobId, null,
                        VulnerabilityType.INFORMATION_DISCLOSURE.name(), "MEDIUM",
                        "Verbose Error / Path Traversal tại " + url,
                        "Server trả về stack trace hoặc system file content khi nhận input sai.",
                        "Stack trace tiết lộ cấu trúc code, framework, và dependencies.",
                        "Custom error pages. Không expose stack trace in production. Block path traversal.",
                        "URL: " + badUrl + "\nContains sensitive error info.",
                        "OPEN", LocalDateTime.now()));
                }
            }
        } catch (Exception ignored) {}
        return results;
    }

    private HttpResponse<String> sendGet(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "HQC-Security-Scanner/1.0")
                    .GET()
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            return null;
        }
    }
}
