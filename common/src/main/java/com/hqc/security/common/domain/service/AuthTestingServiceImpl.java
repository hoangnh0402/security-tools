package com.hqc.security.common.domain.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hqc.security.common.domain.model.Vulnerability;
import com.hqc.security.common.domain.model.VulnerabilityType;
import com.hqc.security.common.domain.port.in.AuthTestingService;
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

/**
 * Service Core cho việc kiểm thử Authentication (Auth Test).
 * Thực hiện: Brute-force Login, JWT Alg None, JWT Weak Secret, JWT Missing Exp.
 */
@Named
public class AuthTestingServiceImpl implements AuthTestingService {

    private static final Logger log = LoggerFactory.getLogger(AuthTestingServiceImpl.class);
    private final VulnerabilityRepository vulnerabilityRepository;
    private final HttpClient httpClient;

    private static final String[] WEAK_SECRETS = {"secret", "password123", "123456", "admin123", "secretkey"};
    
    @Inject
    public AuthTestingServiceImpl(VulnerabilityRepository vulnerabilityRepository) {
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    @Override
    public List<Vulnerability> testAuthentication(UUID projectId, UUID scanJobId, String loginUrl, String targetUrl) {
        List<Vulnerability> allVulns = new ArrayList<>();
        log.info("🔐 Starting Auth Testing — loginUrl: {}, targetUrl: {}", loginUrl, targetUrl);

        if (loginUrl != null && !loginUrl.isBlank()) {
            allVulns.addAll(testBruteForce(loginUrl, scanJobId));
        }

        if (targetUrl != null && !targetUrl.isBlank()) {
            // Giả lập extracted JWT token trên target URL (Trong thực tế crawler cần extract token này)
            // Vì context thử nghiệm độc lập nên ta fake token hoặc bỏ qua nếu user ko truyền token.
            // Để mock MVP: thử fetch JWT từ Authorization header của dummy call if possible.
        }

        if (!allVulns.isEmpty()) {
            vulnerabilityRepository.saveAll(allVulns);
            log.info("💾 Saved {} Auth Testing vulnerabilities", allVulns.size());
        }
        return allVulns;
    }

    /**
     * Test 1: Rate Limit on Login Endpoint / Brute Force
     * Gửi 20 username/password requests liên tiếp, kiểm tra HTTP 429.
     */
    private List<Vulnerability> testBruteForce(String loginUrl, UUID scanJobId) {
        List<Vulnerability> results = new ArrayList<>();
        try {
            int successCount = 0;
            for (int i = 0; i < 20; i++) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(loginUrl))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"admin\",\"password\":\"wrong" + i + "\"}"))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 429) {
                    successCount++;
                }
            }
            if (successCount >= 18) {
                results.add(new Vulnerability(null, scanJobId, null,
                        VulnerabilityType.RATE_LIMIT_BYPASS.name(), "HIGH",
                        "No Rate Limit on Login Endpoint — " + loginUrl,
                        "API Đăng nhập không giới hạn số lỗi (Rate Limit).",
                        "Kẻ tấn công có thể thực hiện dò tìm mật khẩu (Brute-force) liên tục.",
                        "Implement Login Rate Limit cơ chế IP-based hoặc Account-lockout sau N lần.",
                        "URL: " + loginUrl + "\nTested 20 attempts without 429 status.",
                        "OPEN", LocalDateTime.now()));
            }
        } catch (Exception ignored) {
            log.debug("  ⏭️ Skip {} — Exception", loginUrl);
        }
        return results;
    }

    /**
     * Test 2: JWT Decoder logic — Phát hiện Weak Secret, Alg: None, No Exp
     */
    public List<Vulnerability> analyzeJwtToken(String token, UUID scanJobId, String targetUrl) {
        List<Vulnerability> results = new ArrayList<>();
        try {
            DecodedJWT jwt = JWT.decode(token);
            
            // 1. Weak Secret
            for (String secret : WEAK_SECRETS) {
                try {
                    Algorithm alg = Algorithm.HMAC256(secret);
                    alg.verify(jwt); // Nếu không throw, token xài secret yếu
                    results.add(new Vulnerability(null, scanJobId, null,
                            VulnerabilityType.WEAK_JWT.name(), "CRITICAL",
                            "JWT Weak Signature Secret phát hiện tại — " + targetUrl,
                            "Token ký với secret yếu: " + secret,
                            "Kẻ tấn công có thể tạo token mạo danh user tùy ý.",
                            "Sử dụng JWT secret dài trên 256 bits, được gen ngẫu nhiên.",
                            "Token: " + token + "\nCracked Secret: " + secret,
                            "OPEN", LocalDateTime.now()));
                    break;
                } catch (Exception ignored) {}
            }

            // 2. Alg none check
            if ("none".equalsIgnoreCase(jwt.getAlgorithm())) {
                results.add(new Vulnerability(null, scanJobId, null,
                        VulnerabilityType.WEAK_JWT.name(), "CRITICAL",
                        "JWT Alg: none Accepted tại — " + targetUrl,
                        "Hệ thống chấp nhận Json Web Token với alg: none (không chữ ký).",
                        "Kẻ tấn công bypass xác thực dễ dàng.",
                        "Từ chối JWT không chữ ký, ràng buộc header Alg.",
                        "Token: " + token,
                        "OPEN", LocalDateTime.now()));
            }

            // 3. No expiration
            if (jwt.getExpiresAt() == null) {
                results.add(new Vulnerability(null, scanJobId, null,
                        VulnerabilityType.WEAK_JWT.name(), "MEDIUM",
                        "JWT Missing Expiration Date (exp) — " + targetUrl,
                        "Token không bao giờ hết hạn.",
                        "Nếu cookie hay token bị đánh cắp, hacker dùng vĩnh viễn.",
                        "Luôn set thời hạn exp ngắn trong payload.",
                        "Token: " + token,
                        "OPEN", LocalDateTime.now()));
            }

        } catch (Exception e) {
            log.debug("Not a valid JWT: " + e.getMessage());
        }
        return results;
    }
}
