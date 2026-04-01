package com.hqc.security.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "scan_jobs")
@Getter
@Setter
@NoArgsConstructor
public class ScanJobJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "scan_type", nullable = false, length = 50)
    private String scanType;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
}
