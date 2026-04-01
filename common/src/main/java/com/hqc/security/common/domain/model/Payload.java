package com.hqc.security.common.domain.model;

import java.util.UUID;

public record Payload(
    UUID id,
    String type,
    String value,
    boolean isActive
) {}
