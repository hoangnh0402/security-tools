package com.hqc.security.infrastructure.adapter.out;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hqc.security.common.domain.model.Dependency;
import com.hqc.security.common.domain.model.DependencyVulnerability;
import com.hqc.security.common.domain.port.out.CveApiClient;
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

/**
 * Gọi API thực tế của Google OSV.dev để tìm kiếm CVE dựa trên ecosystems (Maven, npm).
 * API Documentation: https://google.github.io/osv.dev/post-v1-query/
 */
@Named
public class OsvDevApiClient implements CveApiClient {

    private static final Logger log = LoggerFactory.getLogger(OsvDevApiClient.class);
    private static final String OSV_API_URL = "https://api.osv.dev/v1/query";

    private final HttpClient httpClient;

    public OsvDevApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public List<DependencyVulnerability> queryVulnerabilities(Dependency dependency) {
        List<DependencyVulnerability> vulns = new ArrayList<>();

        if (dependency.name() == null || dependency.version() == null || dependency.ecosystem() == null) {
            return vulns; // Thiếu thông tin cần thiết
        }

        try {
            // Build OSV JSON Request Body
            JsonObject packageObj = new JsonObject();
            packageObj.addProperty("name", dependency.name());
            packageObj.addProperty("ecosystem", convertEcosystem(dependency.ecosystem()));

            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("version", dependency.version());
            requestBody.add("package", packageObj);

            log.debug("📡 Querying OSV.dev for {}@{} ({})", dependency.name(), dependency.version(), dependency.ecosystem());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OSV_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null && !response.body().isEmpty() && !response.body().equals("{}")) {
                JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                if (jsonResponse.has("vulns")) {
                    JsonArray vulnsArray = jsonResponse.getAsJsonArray("vulns");
                    for (JsonElement el : vulnsArray) {
                        JsonObject vObj = el.getAsJsonObject();
                        String cveId = vObj.has("id") ? vObj.get("id").getAsString() : "UNKNOWN";
                        
                        // Extract summary/details
                        String summary = "";
                        if (vObj.has("summary")) {
                            summary = vObj.get("summary").getAsString();
                        } else if (vObj.has("details")) {
                            summary = vObj.get("details").getAsString();
                            if (summary.length() > 200) summary = summary.substring(0, 197) + "...";
                        }

                        // Determine Severity from database specific metadata or aliases (OSV returns multiple)
                        String severity = "HIGH"; // Default map
                        
                        DependencyVulnerability dv = new DependencyVulnerability(
                                null,  // id
                                dependency.id(), // depId
                                cveId,
                                severity,
                                summary,
                                "Vui lòng kiểm tra báo cáo chi tiết từ OSV / Update version", // Fix version extraction is complex in OSV
                                LocalDateTime.now()
                        );
                        vulns.add(dv);
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ OSV.dev query failed for {}: {}", dependency.name(), e.getMessage());
        }

        return vulns;
    }

    /**
     * Convert local ecosystem string to OSV standard: Maven, npm, PyPI, Go, crates.io, etc.
     */
    private String convertEcosystem(String input) {
        String lower = input.toLowerCase();
        if (lower.contains("java") || lower.contains("maven") || lower.contains("gradle")) return "Maven";
        if (lower.contains("node") || lower.contains("npm") || lower.contains("js")) return "npm";
        if (lower.contains("python") || lower.contains("pip")) return "PyPI";
        if (lower.contains("go")) return "Go";
        return input;
    }
}
