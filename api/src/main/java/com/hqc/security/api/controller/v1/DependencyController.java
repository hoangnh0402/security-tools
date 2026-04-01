package com.hqc.security.api.controller.v1;

import com.hqc.security.api.dto.request.DependencyRequest;
import com.hqc.security.common.domain.model.Dependency;
import com.hqc.security.common.domain.model.DependencyVulnerability;
import com.hqc.security.common.domain.port.in.DependencyScannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/dependencies")
@RequiredArgsConstructor
public class DependencyController {

    private final DependencyScannerService scannerService;

    @PostMapping("/scan")
    public List<DependencyVulnerability> scanDependencies(
            @PathVariable UUID projectId,
            @RequestBody @Valid List<DependencyRequest> requests) {
            
        List<Dependency> dependencies = requests.stream()
                .map(req -> new Dependency(null, projectId, req.name(), req.version(), req.ecosystem(), null))
                .collect(Collectors.toList());
                
        return scannerService.scanDependencies(projectId, dependencies);
    }
}
