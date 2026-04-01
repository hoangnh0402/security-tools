package com.hqc.security.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "payloads")
@Getter
@Setter
@NoArgsConstructor
public class PayloadJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private String value;

    @Column(name = "is_active")
    private boolean isActive = true;
}
