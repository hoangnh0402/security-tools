package com.hqc.security.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "scan_targets")
@Getter
@Setter
@NoArgsConstructor
public class ScanTargetJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "scan_job_id", nullable = false)
    private UUID scanJobId;

    @Column(name = "target_url", nullable = false)
    private String targetUrl;

    @Column(length = 10)
    private String method;

    @Column(columnDefinition = "jsonb")
    private String headers;

    private String body;
}
