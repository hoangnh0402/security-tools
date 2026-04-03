package com.hqc.security.api.controller.v1;

import com.hqc.security.api.dto.request.AuthTestRequest;
import com.hqc.security.common.domain.model.Vulnerability;
import com.hqc.security.common.domain.port.in.AuthTestingService;
import com.hqc.security.common.domain.service.AuthTestingServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST Controller for Authentication Testing (Brute-force, JWT Analysis).
 */
@RestController
@RequestMapping("/api/v1/auth-testing")
@RequiredArgsConstructor
public class AuthTestController {

    private final AuthTestingService authTestingService;

    @PostMapping("/scan")
    @ResponseStatus(HttpStatus.OK)
    public List<Vulnerability> runAuthTest(@RequestBody @Valid AuthTestRequest request) {
        List<Vulnerability> allVulns = new ArrayList<>();
        
        // Brute-force & basic auth testing
        if (request.loginUrl() != null && !request.loginUrl().isBlank()) {
            allVulns.addAll(authTestingService.testAuthentication(
                    request.projectId(), request.scanJobId(), request.loginUrl(), request.targetUrl()));
        }

        // Specific JWT offline analysis
        if (request.jwtToken() != null && !request.jwtToken().isBlank()) {
            if (authTestingService instanceof AuthTestingServiceImpl impl) {
                allVulns.addAll(impl.analyzeJwtToken(request.jwtToken(), request.scanJobId(), request.targetUrl()));
            }
        }
        
        return allVulns;
    }
}
