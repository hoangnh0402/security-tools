package com.hqc.security.api.controller.v1;

import com.hqc.security.api.dto.request.CreateScanJobRequest;
import com.hqc.security.api.dto.response.ScanJobResponse;
import com.hqc.security.application.executor.AsyncJobExecutor;
import com.hqc.security.common.domain.model.ScanJob;
import com.hqc.security.common.domain.port.in.ScanJobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/scan-jobs")
@RequiredArgsConstructor
public class ScanJobController {

    private final ScanJobService scanJobService;
    private final AsyncJobExecutor asyncJobExecutor;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScanJobResponse startScanJob(@RequestBody @Valid CreateScanJobRequest request) {
        // 1. Lưu Job vào CSDL với trạng thái PENDING
        ScanJob job = scanJobService.createJob(
                request.projectId(), 
                request.initiatorId(), 
                request.scanType()
        );
        
        // 2. Kích hoạt Scanner & Crawler ẩn danh trong Background Thread
        asyncJobExecutor.executeScanJobAsync(job.id(), request.targetUrl());
        
        // 3. Phản hồi PENDING cho người dùng ngay lập tức (Non-blocking)
        return ScanJobResponse.fromDomain(job);
    }

    @GetMapping("/{id}")
    public ScanJobResponse getScanJobStatus(@PathVariable UUID id) {
        ScanJob job = scanJobService.getScanStatus(id);
        return ScanJobResponse.fromDomain(job);
    }
}
