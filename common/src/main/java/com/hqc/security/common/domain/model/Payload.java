package com.hqc.security.common.domain.model;

import java.util.UUID;

public record Payload(
    UUID id,
    String type,
    String value,
    String context,   // URL, HTML, JSON, HEADER
    String encoding,  // NONE, URL_ENCODE, BASE64, DOUBLE_ENCODE
    boolean isActive
) {}
