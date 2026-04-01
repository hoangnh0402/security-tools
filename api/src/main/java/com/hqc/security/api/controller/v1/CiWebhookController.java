package com.hqc.security.api.controller.v1;

import com.hqc.security.api.dto.request.CreateScanJobRequest;
import com.hqc.security.common.domain.model.CiScanResult;
import com.hqc.security.common.domain.port.in.CiIntegrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ci-webhook")
@RequiredArgsConstructor
public class CiWebhookController {

    private final CiIntegrationService ciIntegrationService;

    @PostMapping("/scan-and-wait")
    public ResponseEntity<CiScanResult> triggerBlockingScanJob(@RequestBody @Valid CreateScanJobRequest request) {
        
        // Cần lưu ý: Connection TCP ở đây sẽ bị kẹt lại chờ phản hồi từ Cỗ máy Dò Quét. 
        CiScanResult result = ciIntegrationService.triggerScanAndWait(
                request.projectId(), 
                request.initiatorId(), 
                request.targetUrl()
        );

        // Ném HTTP 406 Not Acceptable sẽ kích hoạt thuật toán đánh RỚT Pipeline của Gitlab Runner.
        if (!result.success()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(result);
        }

        return ResponseEntity.ok(result);
    }
}
