package com.hqc.security.application.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Có thể cấu hình ThreadPoolTaskExecutor để chặn limit số lượng Scan chạy đồng thời tại đây.
}
