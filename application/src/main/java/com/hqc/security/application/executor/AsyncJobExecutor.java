package com.hqc.security.application.executor;

import com.hqc.security.common.domain.port.in.ScanJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsyncJobExecutor {

    private final ScanJobService scanJobService;

    @Async
    public void executeScanJobAsync(UUID scanJobId, String targetUrl) {
        // Hàm này sẽ được Spring chuyển cho Thread rảnh rỗi thực thi, 
        // không làm trì hoãn HTTP Response trả về User.
        scanJobService.executeJob(scanJobId, targetUrl);
    }
}
