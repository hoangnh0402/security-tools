package com.hqc.security.crawler.adapter.out;

import com.hqc.security.common.domain.model.Endpoint;
import com.hqc.security.common.domain.port.out.AssetDiscoveryPort;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import jakarta.inject.Named;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CrawlerAdapter — Spider/Crawler nâng cao tương tự ZAP Spider.
 * Hỗ trợ: BFS crawling, depth limit, JS endpoint extraction, Swagger parsing.
 */
@Named
public class CrawlerAdapter implements AssetDiscoveryPort {

    private static final Logger log = LoggerFactory.getLogger(CrawlerAdapter.class);

    private static final int MAX_DEPTH = 3;
    private static final int MAX_URLS = 500;
    private static final int TIMEOUT_MS = 10_000;

    private static final String[] USER_AGENTS = {
        "HQC-Security-Scanner/1.0",
        "Mozilla/5.0 (compatible; HQC-Bot/1.0)",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
    };

    /** Regex patterns để extract API endpoints từ JavaScript */
    private static final List<Pattern> JS_API_PATTERNS = List.of(
        Pattern.compile("(?:fetch|axios\\.\\w+|\\$\\.\\w+)\\s*\\(\\s*['\"`]([^'\"` ]+)['\"`]"),
        Pattern.compile("['\"`](/api/[^'\"` ]+)['\"`]"),
        Pattern.compile("['\"`](https?://[^'\"` ]+/api/[^'\"` ]+)['\"`]"),
        Pattern.compile("url\\s*[:=]\\s*['\"`]([^'\"` ]+)['\"`]")
    );

    @Override
    public List<Endpoint> crawlEndpoints(String seedUrl, UUID projectId) {
        if (seedUrl.endsWith(".json") || seedUrl.endsWith(".yaml") || seedUrl.endsWith(".yml")) {
            return crawlSwaggerApi(seedUrl, projectId);
        }
        return crawlWebBfs(seedUrl, projectId);
    }

    // ============================================================
    // BFS Web Crawler
    // ============================================================

    private List<Endpoint> crawlWebBfs(String seedUrl, UUID projectId) {
        List<Endpoint> endpoints = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Queue<UrlWithDepth> queue = new LinkedList<>();

        String baseDomain = extractDomain(seedUrl);
        queue.add(new UrlWithDepth(seedUrl, 0));
        visited.add(seedUrl);

        log.info("🕷️ Starting BFS Crawl — seed: {}, maxDepth: {}, maxUrls: {}", seedUrl, MAX_DEPTH, MAX_URLS);

        while (!queue.isEmpty() && endpoints.size() < MAX_URLS) {
            UrlWithDepth current = queue.poll();
            if (current.depth() > MAX_DEPTH) continue;

            try {
                String userAgent = USER_AGENTS[current.depth() % USER_AGENTS.length];
                Document doc = Jsoup.connect(current.url())
                        .userAgent(userAgent)
                        .timeout(TIMEOUT_MS)
                        .followRedirects(true)
                        .ignoreHttpErrors(true)
                        .get();

                // 1. Extract links
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String absHref = link.attr("abs:href");
                    if (isValidUrl(absHref, baseDomain) && !visited.contains(absHref)) {
                        visited.add(absHref);
                        endpoints.add(new Endpoint(null, projectId, absHref, "GET", "[]", LocalDateTime.now()));
                        if (current.depth() < MAX_DEPTH) {
                            queue.add(new UrlWithDepth(absHref, current.depth() + 1));
                        }
                    }
                }

                // 2. Extract forms
                Elements forms = doc.select("form");
                for (Element form : forms) {
                    String action = form.attr("abs:action");
                    if (action.isEmpty()) action = current.url();
                    String method = form.attr("method").toUpperCase();
                    if (method.isEmpty()) method = "GET";

                    Elements inputs = form.select("input, textarea, select");
                    String params = buildParamsJson(inputs);

                    if (!visited.contains(action + "#" + method)) {
                        visited.add(action + "#" + method);
                        endpoints.add(new Endpoint(null, projectId, action, method, params, LocalDateTime.now()));
                    }
                }

                // 3. Extract API endpoints from inline/external JavaScript
                extractJsEndpoints(doc, current.url(), baseDomain, projectId, endpoints, visited);

            } catch (IOException e) {
                log.debug("  ⏭️ Skip {} — {}", current.url(), e.getMessage());
            }
        }

        log.info("✅ BFS Crawl complete — {} endpoints discovered", endpoints.size());
        return endpoints;
    }

    private void extractJsEndpoints(Document doc, String pageUrl, String baseDomain,
            UUID projectId, List<Endpoint> endpoints, Set<String> visited) {
        // Inline scripts
        Elements scripts = doc.select("script");
        for (Element script : scripts) {
            String scriptContent = script.data();
            if (!scriptContent.isBlank()) {
                extractApiUrlsFromJs(scriptContent, pageUrl, baseDomain, projectId, endpoints, visited);
            }
        }
    }

    private void extractApiUrlsFromJs(String jsContent, String pageUrl, String baseDomain,
            UUID projectId, List<Endpoint> endpoints, Set<String> visited) {
        for (Pattern pattern : JS_API_PATTERNS) {
            Matcher matcher = pattern.matcher(jsContent);
            while (matcher.find() && endpoints.size() < MAX_URLS) {
                String apiPath = matcher.group(1);

                // Resolve relative paths
                if (apiPath.startsWith("/")) {
                    try {
                        URI base = URI.create(pageUrl);
                        apiPath = base.getScheme() + "://" + base.getHost()
                                + (base.getPort() > 0 ? ":" + base.getPort() : "") + apiPath;
                    } catch (Exception ignored) { continue; }
                }

                if (apiPath.startsWith("http") && !visited.contains(apiPath)) {
                    visited.add(apiPath);
                    endpoints.add(new Endpoint(null, projectId, apiPath, "GET", "[]", LocalDateTime.now()));
                }
            }
        }
    }

    // ============================================================
    // Swagger/OpenAPI Parser (giữ nguyên logic cũ, thêm log)
    // ============================================================

    private List<Endpoint> crawlSwaggerApi(String seedUrl, UUID projectId) {
        List<Endpoint> endpoints = new ArrayList<>();
        try {
            OpenAPI openAPI = new OpenAPIV3Parser().read(seedUrl);
            if (openAPI != null && openAPI.getPaths() != null) {
                String baseUrl = seedUrl.substring(0, seedUrl.lastIndexOf("/"));

                for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
                    String path = entry.getKey();
                    PathItem pathItem = entry.getValue();
                    String fullUrl = baseUrl + path;

                    if (pathItem.getGet() != null)
                        endpoints.add(buildEndpointFromOperation(fullUrl, "GET", pathItem.getGet(), projectId));
                    if (pathItem.getPost() != null)
                        endpoints.add(buildEndpointFromOperation(fullUrl, "POST", pathItem.getPost(), projectId));
                    if (pathItem.getPut() != null)
                        endpoints.add(buildEndpointFromOperation(fullUrl, "PUT", pathItem.getPut(), projectId));
                    if (pathItem.getDelete() != null)
                        endpoints.add(buildEndpointFromOperation(fullUrl, "DELETE", pathItem.getDelete(), projectId));
                    if (pathItem.getPatch() != null)
                        endpoints.add(buildEndpointFromOperation(fullUrl, "PATCH", pathItem.getPatch(), projectId));
                }
            }
            log.info("📋 Swagger/OpenAPI parsed — {} endpoints from {}", endpoints.size(), seedUrl);
        } catch (Exception e) {
            log.error("❌ Cannot parse API Spec: {} — {}", seedUrl, e.getMessage());
        }
        return endpoints;
    }

    // ============================================================
    // Helpers
    // ============================================================

    private Endpoint buildEndpointFromOperation(String fullUrl, String method, Operation op, UUID projectId) {
        StringBuilder params = new StringBuilder("[");
        if (op.getParameters() != null) {
            for (int i = 0; i < op.getParameters().size(); i++) {
                Parameter p = op.getParameters().get(i);
                params.append("\"").append(p.getName()).append("\"");
                if (i < op.getParameters().size() - 1) params.append(",");
            }
        }
        params.append("]");
        return new Endpoint(null, projectId, fullUrl, method, params.toString(), LocalDateTime.now());
    }

    private boolean isValidUrl(String url, String baseDomain) {
        if (url == null || url.isBlank()) return false;
        if (!url.startsWith("http")) return false;
        if (url.contains("#")) url = url.substring(0, url.indexOf("#"));
        // Same domain check
        return extractDomain(url).equals(baseDomain);
    }

    private String extractDomain(String url) {
        try {
            URI uri = URI.create(url);
            return uri.getHost() != null ? uri.getHost().toLowerCase() : "";
        } catch (Exception e) {
            return "";
        }
    }

    private String buildParamsJson(Elements inputs) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < inputs.size(); i++) {
            String name = inputs.get(i).attr("name");
            if (!name.isEmpty()) {
                if (sb.length() > 1) sb.append(",");
                sb.append("\"").append(name).append("\"");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private record UrlWithDepth(String url, int depth) {}
}
