package com.hqc.security.api.controller.v1;

import com.hqc.security.common.domain.model.ReportSummary;
import com.hqc.security.common.domain.port.in.ReportExportService;
import com.hqc.security.common.domain.port.in.ReportUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportUseCase reportUseCase;
    private final ReportExportService reportExportService;

    // Xem tổng kết Dashboard (Pie chart, Bar chart data)
    @GetMapping("/projects/{projectId}/summary")
    public ReportSummary getProjectSummary(@PathVariable UUID projectId) {
        return reportUseCase.getProjectSummary(projectId);
    }

    // Tải Download File báo cáo gốc của 1 đợt Scan bất kỳ
    @GetMapping("/scan-jobs/{scanJobId}/export-csv")
    public ResponseEntity<byte[]> exportCsv(@PathVariable UUID scanJobId) {
        byte[] fileData = reportExportService.exportScanJobCsv(scanJobId);

        return ResponseEntity.ok()
                // Browser sẽ tự động popup bật cửa sổ Tải file
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"scan-report-" + scanJobId + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(fileData);
    }
}
