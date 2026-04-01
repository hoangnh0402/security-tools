package com.hqc.security.common.domain.service;

import com.hqc.security.common.domain.model.Vulnerability;
import com.hqc.security.common.domain.port.in.ReportExportService;
import com.hqc.security.common.domain.port.out.VulnerabilityRepository;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Named
public class CsvReportExportServiceImpl implements ReportExportService {

    private final VulnerabilityRepository vulnerabilityRepository;

    @Inject
    public CsvReportExportServiceImpl(VulnerabilityRepository vulnerabilityRepository) {
        this.vulnerabilityRepository = vulnerabilityRepository;
    }

    @Override
    public byte[] exportScanJobCsv(UUID scanJobId) {
        List<Vulnerability> vulns = vulnerabilityRepository.findAllByScanJobId(scanJobId);
        
        StringBuilder csvBuilder = new StringBuilder();
        // Cần gắn thẻ BOM UTF-8 để Microsoft Excel mở tiếng Việt không bị lỗi font kí tự unicode
        csvBuilder.append('\ufeff');

        // Headers
        csvBuilder.append("ID,Mức độ (Severity),Mảng Tấn Công (Type),Chẩn đoán (Title),Mô tả Chi Tiết (Description),Trạng thái,Ngày quét\n");

        for (Vulnerability v : vulns) {
            csvBuilder.append(escapeSpecialCharacters(v.id().toString())).append(",");
            csvBuilder.append(escapeSpecialCharacters(v.severity())).append(",");
            csvBuilder.append(escapeSpecialCharacters(v.type())).append(",");
            csvBuilder.append(escapeSpecialCharacters(v.title())).append(",");
            csvBuilder.append(escapeSpecialCharacters(v.description())).append(",");
            csvBuilder.append(escapeSpecialCharacters(v.status())).append(",");
            csvBuilder.append(escapeSpecialCharacters(v.createdAt().toString())).append("\n");
        }

        return csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null) return "";
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
