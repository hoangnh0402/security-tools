package com.hqc.security.common.domain.port.in;

import com.hqc.security.common.domain.model.User;
import com.hqc.security.common.domain.model.Role;
import java.util.UUID;

public interface UserService {
    User createUser(String username, String email, String passwordHash, Role role);
    User getUserById(UUID id);
    void deleteUser(UUID id);
}
