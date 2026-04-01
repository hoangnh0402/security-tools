package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.Project;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    List<Project> findAllByCreatedBy(UUID userId);
    void deleteById(UUID id);
}
