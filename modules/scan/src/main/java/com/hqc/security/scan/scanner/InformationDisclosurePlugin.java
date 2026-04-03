package com.hqc.security.scan.scanner;

import com.hqc.security.common.domain.model.Vulnerability;
import com.hqc.security.common.domain.model.VulnerabilityType;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Information Disclosure Scanner — Detect server version leak, stack trace, sensitive data.
 * Tương tự ZAP "Information Disclosure" Passive Scan Rules.
 */
@Named
public class InformationDisclosurePlugin implements PassiveScanPlugin {

    private static final List<Pattern> SENSITIVE_PATTERNS = List.of(
        Pattern.compile("(?i)stack\\s*trace"),
        Pattern.compile("(?i)exception\\s+in\\s+thread"),
        Pattern.compile("(?i)at\\s+[a-z]+\\.[a-z]+\\.[a-z]+\\("),  // Java stack trace
        Pattern.compile("(?i)traceback\\s*\\(most recent"),           // Python traceback
        Pattern.compile("(?i)fatal\\s+error"),
        Pattern.compile("(?i)internal\\s+server\\s+error"),
        Pattern.compile("\\b\\d{3}[-.]\\d{3}[-.]\\d{4}\\b"),         // Phone number
        Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") // Email
    );

    private static final String[] SERVER_HEADERS = {"server", "x-powered-by", "x-aspnet-version"};

    @Override
    public String getName() { return "Information Disclosure Scanner"; }

    @Override
    public List<Vulnerability> analyze(String url, int statusCode, Map<String, List<String>> headers, String body, UUID scanJobId) {
        List<Vulnerability> results = new ArrayList<>();

        // 1. Check server version disclosure in headers
        var lowerHeaders = new java.util.HashMap<String, List<String>>();
        headers.forEach((k, v) -> lowerHeaders.put(k.toLowerCase(), v));

        for (String header : SERVER_HEADERS) {
            List<String> values = lowerHeaders.getOrDefault(header, List.of());
            for (String value : values) {
                if (value.matches(".*[0-9]+\\.[0-9]+.*")) { // Contains version number
                    results.add(new Vulnerability(null, scanJobId, null,
                        VulnerabilityType.INFORMATION_DISCLOSURE.name(), "LOW",
                        "Server Version Disclosure — " + header + ": " + value + " — " + url,
                        "HTTP response header '" + header + "' tiết lộ phiên bản server/framework.",
                        "Kẻ tấn công có thể xác định phiên bản phần mềm và tìm CVE tương ứng.",
                        "Xóa hoặc ẩn version number trong '" + header + "' header.",
                        header + ": " + value,
                        "OPEN", LocalDateTime.now()));
                }
            }
        }

        // 2. Check sensitive patterns in response body
        if (body != null && !body.isEmpty()) {
            for (Pattern pattern : SENSITIVE_PATTERNS) {
                var matcher = pattern.matcher(body);
                if (matcher.find()) {
                    String match = matcher.group();
                    if (match.length() > 100) match = match.substring(0, 100) + "...";
                    results.add(new Vulnerability(null, scanJobId, null,
                        VulnerabilityType.INFORMATION_DISCLOSURE.name(), "MEDIUM",
                        "Information Disclosure tại " + url,
                        "Response body chứa thông tin nhạy cảm matching pattern: " + pattern.pattern(),
                        "Stack trace, error message, hoặc PII bị lộ trong response.",
                        "Custom error pages không hiển thị stack trace. Validate output không chứa sensitive data.",
                        "Pattern: " + pattern.pattern() + "\nMatch: " + match,
                        "OPEN", LocalDateTime.now()));
                    break; // 1 match per pattern type
                }
            }
        }

        return results;
    }
}
