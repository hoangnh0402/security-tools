package com.hqc.security.api.controller.v1;

import com.hqc.security.api.dto.request.ApiSecurityTestRequest;
import com.hqc.security.common.domain.model.Vulnerability;
import com.hqc.security.common.domain.port.in.ApiSecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho API Security Testing — tương tự ZAP API Scan tab.
 */
@RestController
@RequestMapping("/api/v1/api-security")
@RequiredArgsConstructor
public class ApiSecurityController {

    private final ApiSecurityService apiSecurityService;

    @PostMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public List<Vulnerability> runApiSecurityTest(@RequestBody @Valid ApiSecurityTestRequest request) {
        return apiSecurityService.testApiSecurity(
                request.projectId(), request.scanJobId(), request.endpointUrls());
    }
}
