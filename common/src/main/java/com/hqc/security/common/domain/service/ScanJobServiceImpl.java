package com.hqc.security.common.domain.service;

import com.hqc.security.common.domain.model.Endpoint;
import com.hqc.security.common.domain.model.ScanJob;
import com.hqc.security.common.domain.model.ScanJobStatus;
import com.hqc.security.common.domain.port.in.ScanJobService;
import com.hqc.security.common.domain.port.out.AssetDiscoveryPort;
import com.hqc.security.common.domain.port.out.EndpointRepository;
import com.hqc.security.common.domain.port.out.ScanJobRepository;
import com.hqc.security.common.domain.port.out.VulnerabilityScannerPort;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Named
public class ScanJobServiceImpl implements ScanJobService {
    private final ScanJobRepository scanJobRepository;
    private final EndpointRepository endpointRepository;
    private final AssetDiscoveryPort assetDiscoveryPort;
    private final VulnerabilityScannerPort vulnerabilityScannerPort;

    @Inject
    public ScanJobServiceImpl(
            ScanJobRepository scanJobRepository, 
            EndpointRepository endpointRepository, 
            AssetDiscoveryPort assetDiscoveryPort,
            VulnerabilityScannerPort vulnerabilityScannerPort) {
        this.scanJobRepository = scanJobRepository;
        this.endpointRepository = endpointRepository;
        this.assetDiscoveryPort = assetDiscoveryPort;
        this.vulnerabilityScannerPort = vulnerabilityScannerPort;
    }

    @Override
    public ScanJob createJob(UUID projectId, UUID initiatorId, String scanType) {
        ScanJob job = new ScanJob(null, projectId, initiatorId, scanType, ScanJobStatus.PENDING, null, null);
        return scanJobRepository.save(job);
    }

    @Override
    public void executeJob(UUID scanJobId, String targetUrl) {
        // 1. Chuyển trạng thái Job sang RUNNING, bắt đầu block thời gian
        scanJobRepository.updateStatus(scanJobId, ScanJobStatus.RUNNING);
        
        ScanJob job = scanJobRepository.findById(scanJobId)
                .orElseThrow(() -> new IllegalStateException("Job not found"));

        try {
            // 2. Chạy Crawler thông qua Interface (Dependency Inversion). 
            // Core logic không quan tâm bên dưới xài công nghệ gì (JSoup hay Selenium).
            List<Endpoint> endpoints = assetDiscoveryPort.crawlEndpoints(targetUrl, job.projectId());
            
            // 3. Móc vào Repository để lưu kết quả Endpoint vào CSDL
            if (endpoints != null && !endpoints.isEmpty()) {
                endpointRepository.saveAll(endpoints);
                
                // Thực thi Vulnerability Scanner bằng đạn Payload trên các Endpoints tìm được
                vulnerabilityScannerPort.scanEndpoints(scanJobId, endpoints);
            }

            // 4. Nếu toàn bộ logic quét thành công, cập nhật State về DONE.
            scanJobRepository.updateStatus(scanJobId, ScanJobStatus.DONE);
            
        } catch (Exception e) {
            // Nếu một trong các tiến trình bị lỗi -> FAILED
            scanJobRepository.updateStatus(scanJobId, ScanJobStatus.FAILED);
        }
    }

    @Override
    public ScanJob getScanStatus(UUID scanJobId) {
        return scanJobRepository.findById(scanJobId)
                .orElseThrow(() -> new IllegalArgumentException("Scan job not found"));
    }
}
