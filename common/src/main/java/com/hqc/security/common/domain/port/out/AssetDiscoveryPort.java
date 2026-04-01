package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.Endpoint;
import java.util.List;
import java.util.UUID;

/**
 * Interface độc lập (Dependency Inversion). Bất kỳ crawler nào
 * (JSoup, Selenium) muốn cắm vào Job Orchestrator đều phải implement port này.
 */
public interface AssetDiscoveryPort {
    List<Endpoint> crawlEndpoints(String seedUrl, UUID projectId);
}
