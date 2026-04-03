package com.hqc.security.scan.scanner;

import com.hqc.security.common.domain.model.Vulnerability;
import com.hqc.security.common.domain.model.VulnerabilityType;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Kiểm tra thiếu Security Headers — tương tự ZAP "Missing Anti-clickjacking Header" etc.
 */
@Named
public class MissingSecurityHeadersPlugin implements PassiveScanPlugin {

    private record HeaderCheck(String header, String description, String severity) {}

    private static final List<HeaderCheck> REQUIRED_HEADERS = List.of(
        new HeaderCheck("x-frame-options", "Thiếu X-Frame-Options → Clickjacking risk", "MEDIUM"),
        new HeaderCheck("x-content-type-options", "Thiếu X-Content-Type-Options → MIME sniffing risk", "LOW"),
        new HeaderCheck("strict-transport-security", "Thiếu HSTS → Downgrade attack risk", "MEDIUM"),
        new HeaderCheck("content-security-policy", "Thiếu CSP → XSS & injection risk", "MEDIUM"),
        new HeaderCheck("x-xss-protection", "Thiếu X-XSS-Protection → Legacy XSS filter disabled", "LOW"),
        new HeaderCheck("referrer-policy", "Thiếu Referrer-Policy → Information leakage risk", "LOW"),
        new HeaderCheck("permissions-policy", "Thiếu Permissions-Policy → Feature policy not defined", "INFO")
    );

    @Override
    public String getName() { return "Missing Security Headers Scanner"; }

    @Override
    public List<Vulnerability> analyze(String url, int statusCode, Map<String, List<String>> headers, String body, UUID scanJobId) {
        List<Vulnerability> results = new ArrayList<>();

        // Normalize header keys to lowercase
        var lowerHeaders = new java.util.HashMap<String, List<String>>();
        headers.forEach((k, v) -> lowerHeaders.put(k.toLowerCase(), v));

        for (HeaderCheck check : REQUIRED_HEADERS) {
            if (!lowerHeaders.containsKey(check.header())) {
                results.add(new Vulnerability(null, scanJobId, null,
                    VulnerabilityType.MISSING_SECURITY_HEADER.name(), check.severity(),
                    check.description() + " — " + url,
                    "Response thiếu header '" + check.header() + "'.",
                    "Browser không được bảo vệ bởi security mechanism tương ứng.",
                    "Thêm header '" + check.header() + "' vào response. Tham khảo OWASP Secure Headers Project.",
                    "URL: " + url + "\nMissing header: " + check.header(),
                    "OPEN", LocalDateTime.now()));
            }
        }

        return results;
    }
}
