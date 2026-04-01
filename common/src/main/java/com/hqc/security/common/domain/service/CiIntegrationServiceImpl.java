package com.hqc.security.common.domain.service;

import com.hqc.security.common.domain.model.CiScanResult;
import com.hqc.security.common.domain.model.ScanJob;
import com.hqc.security.common.domain.model.ScanJobStatus;
import com.hqc.security.common.domain.model.Vulnerability;
import com.hqc.security.common.domain.port.in.CiIntegrationService;
import com.hqc.security.common.domain.port.in.ScanJobService;
import com.hqc.security.common.domain.port.out.VulnerabilityRepository;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.util.List;
import java.util.UUID;

@Named
public class CiIntegrationServiceImpl implements CiIntegrationService {

    private final ScanJobService scanJobService;
    private final VulnerabilityRepository vulnerabilityRepository;

    @Inject
    public CiIntegrationServiceImpl(ScanJobService scanJobService, VulnerabilityRepository vulnerabilityRepository) {
        this.scanJobService = scanJobService;
        this.vulnerabilityRepository = vulnerabilityRepository;
    }

    @Override
    public CiScanResult triggerScanAndWait(UUID projectId, UUID initiatorId, String targetUrl) {
        // 1. Cấp mã lệnh cho một luồng quét Web (Sử dụng Orchestrator lõi)
        ScanJob job = scanJobService.createJob(projectId, initiatorId, "WEB_SCAN");

        // 2. Chạy Cục Bộ (Synchronous / Blocking):
        // Khác với luồng API người dùng bấm qua Web (được bật '@Async'), luồng Webhook do Máy Bot
        // của Jenkins gọi sẽ bị block tại hàm này vì ta ép gọi 'executeJob' chung Thread hiện hành.
        // Thread này sẽ bị treo chờ cho đến khi tiến trình cào và dò lõi hoàn tất. 
        scanJobService.executeJob(job.id(), targetUrl); 

        // 3. Đọc dữ liệu Phán quyết DB
        ScanJob finishedJob = scanJobService.getScanStatus(job.id());
        
        if (finishedJob.status() == ScanJobStatus.FAILED) {
            return new CiScanResult(false, "[LỖI NỘI BỘ] Quét thất bại do timeout/crawler ngỏm.", 0, 0, 0);
        }

        // 4. Kiểm đếm Cảnh Báo
        List<Vulnerability> vulns = vulnerabilityRepository.findAllByScanJobId(job.id());
        int high = 0, medium = 0, low = 0;
        
        for (Vulnerability v : vulns) {
            String sev = v.severity().toUpperCase();
            if (sev.equals("HIGH") || sev.equals("CRITICAL")) high++;
            else if (sev.equals("MEDIUM")) medium++;
            else low++;
        }

        // 5. Kết luận Số Mệnh Pipeline
        boolean pass = (high == 0); // Tiêu chuẩn An toàn MVP: Hễ có lỗi HIGH là rớt mạng.
        String msg = pass ? "An toàn vượt ải rải thảm! (Pipeline PASSED)" : "Phát hiện mã độc/lỗ hổng mức HIGH. Quyết định: Cấm Deploy! (Pipeline FAILED)";
        
        return new CiScanResult(pass, msg, high, medium, low);
    }
}
