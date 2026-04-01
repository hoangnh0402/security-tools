package com.hqc.security.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "endpoints")
@Getter
@Setter
@NoArgsConstructor
public class EndpointJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(columnDefinition = "jsonb")
    private String parameters;

    @CreationTimestamp
    @Column(name = "discovered_at", updatable = false)
    private LocalDateTime discoveredAt;
}
