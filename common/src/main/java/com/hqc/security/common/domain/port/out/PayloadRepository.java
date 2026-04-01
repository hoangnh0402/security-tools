package com.hqc.security.common.domain.port.out;

import com.hqc.security.common.domain.model.Payload;
import java.util.List;

public interface PayloadRepository {
    List<Payload> findAllActive();
    List<Payload> findByType(String type);
}
