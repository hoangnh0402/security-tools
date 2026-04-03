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
 * Cookie Security Scanner — Kiểm tra cookie thiếu Secure/HttpOnly/SameSite.
 * Tương tự ZAP "Cookie Without Secure Flag" Passive Scan Rule.
 */
@Named
public class CookieSecurityPlugin implements PassiveScanPlugin {

    @Override
    public String getName() { return "Cookie Security Scanner"; }

    @Override
    public List<Vulnerability> analyze(String url, int statusCode, Map<String, List<String>> headers, String body, UUID scanJobId) {
        List<Vulnerability> results = new ArrayList<>();

        List<String> cookies = headers.getOrDefault("set-cookie",
                headers.getOrDefault("Set-Cookie", List.of()));

        for (String cookie : cookies) {
            String lower = cookie.toLowerCase();
            String cookieName = cookie.split("=")[0].trim();

            if (!lower.contains("secure")) {
                results.add(new Vulnerability(null, scanJobId, null,
                    VulnerabilityType.INSECURE_COOKIE.name(), "MEDIUM",
                    "Cookie '" + cookieName + "' thiếu Secure flag — " + url,
                    "Cookie được gửi qua HTTP không mã hóa.",
                    "Cookie có thể bị đánh cắp qua man-in-the-middle attack.",
                    "Thêm 'Secure' flag vào Set-Cookie header.",
                    "Cookie: " + cookie,
                    "OPEN", LocalDateTime.now()));
            }

            if (!lower.contains("httponly")) {
                results.add(new Vulnerability(null, scanJobId, null,
                    VulnerabilityType.INSECURE_COOKIE.name(), "LOW",
                    "Cookie '" + cookieName + "' thiếu HttpOnly flag — " + url,
                    "Cookie có thể bị truy cập bởi JavaScript (document.cookie).",
                    "XSS attack có thể đánh cắp cookie session.",
                    "Thêm 'HttpOnly' flag vào Set-Cookie header.",
                    "Cookie: " + cookie,
                    "OPEN", LocalDateTime.now()));
            }

            if (!lower.contains("samesite")) {
                results.add(new Vulnerability(null, scanJobId, null,
                    VulnerabilityType.INSECURE_COOKIE.name(), "LOW",
                    "Cookie '" + cookieName + "' thiếu SameSite attribute — " + url,
                    "Cookie được gửi kèm cross-site requests.",
                    "Tăng rủi ro CSRF attack.",
                    "Thêm 'SameSite=Strict' hoặc 'SameSite=Lax' vào Set-Cookie header.",
                    "Cookie: " + cookie,
                    "OPEN", LocalDateTime.now()));
            }
        }

        return results;
    }
}
