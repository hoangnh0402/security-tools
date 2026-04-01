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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Named
public class CrawlerAdapter implements AssetDiscoveryPort {

    @Override
    public List<Endpoint> crawlEndpoints(String seedUrl, UUID projectId) {
        // Logic Rẻ Nhánh Mới: Swagger API vs Web HTML HTML DOM
        if (seedUrl.endsWith(".json") || seedUrl.endsWith(".yaml")) {
            return crawlSwaggerApi(seedUrl, projectId);
        } else {
            return crawlWebDom(seedUrl, projectId);
        }
    }

    private List<Endpoint> crawlSwaggerApi(String seedUrl, UUID projectId) {
        List<Endpoint> endpoints = new ArrayList<>();
        try {
            OpenAPI openAPI = new OpenAPIV3Parser().read(seedUrl);
            if (openAPI != null && openAPI.getPaths() != null) {
                // Base root (Chưa hỗ trợ gộp host/server trong MVP này)
                String baseUrl = seedUrl.substring(0, seedUrl.lastIndexOf("/"));
                
                for (Map.Entry<String, PathItem> entry : openAPI.getPaths().entrySet()) {
                    String path = entry.getKey();
                    PathItem pathItem = entry.getValue();
                    String fullUrl = baseUrl + path;

                    // Expose GET
                    if (pathItem.getGet() != null) {
                        endpoints.add(buildEndpointFromOperation(fullUrl, "GET", pathItem.getGet(), projectId));
                    }
                    // Expose POST
                    if (pathItem.getPost() != null) {
                        endpoints.add(buildEndpointFromOperation(fullUrl, "POST", pathItem.getPost(), projectId));
                    }
                    // Expose PUT
                    if (pathItem.getPut() != null) {
                        endpoints.add(buildEndpointFromOperation(fullUrl, "PUT", pathItem.getPut(), projectId));
                    }
                    // Expose DELETE
                    if (pathItem.getDelete() != null) {
                        endpoints.add(buildEndpointFromOperation(fullUrl, "DELETE", pathItem.getDelete(), projectId));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[SWAGGER-CRAWL] Không thể parse API Spec: " + e.getMessage());
        }
        return endpoints;
    }

    private Endpoint buildEndpointFromOperation(String fullUrl, String method, Operation operation, UUID projectId) {
        StringBuilder paramsBuilder = new StringBuilder("[");
        if (operation.getParameters() != null) {
            for (int i = 0; i < operation.getParameters().size(); i++) {
                Parameter p = operation.getParameters().get(i);
                paramsBuilder.append("\"").append(p.getName()).append("\"");
                if (i < operation.getParameters().size() - 1) paramsBuilder.append(",");
            }
        }
        paramsBuilder.append("]");
        return new Endpoint(null, projectId, fullUrl, method, paramsBuilder.toString(), LocalDateTime.now());
    }

    private List<Endpoint> crawlWebDom(String seedUrl, UUID projectId) {
        List<Endpoint> endpoints = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(seedUrl)
                    .userAgent("HQC-Security-CrawlerBot/1.0")
                    .timeout(10000)
                    .get();

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String absHref = link.attr("abs:href");
                if (absHref.startsWith("http")) {
                    endpoints.add(new Endpoint(null, projectId, absHref, "GET", "[]", LocalDateTime.now()));
                }
            }

            Elements forms = doc.select("form");
            for (Element form : forms) {
                String action = form.attr("abs:action");
                if (action.isEmpty()) { action = seedUrl; }
                String method = form.attr("method").toUpperCase();
                if (method.isEmpty()) { method = "GET"; }
                
                Elements inputs = form.select("input");
                StringBuilder paramsBuilder = new StringBuilder("[");
                for (int i = 0; i < inputs.size(); i++) {
                    paramsBuilder.append("\"").append(inputs.get(i).attr("name")).append("\"");
                    if (i < inputs.size() - 1) paramsBuilder.append(",");
                }
                paramsBuilder.append("]");
                
                endpoints.add(new Endpoint(null, projectId, action, method, paramsBuilder.toString(), LocalDateTime.now()));
            }
        } catch (IOException e) {
            System.err.println("[CRAWLER] Lỗi trích xuất URL nội bộ: " + e.getMessage());
        }
        return endpoints;
    }
}
