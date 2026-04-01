package com.hqc.security.api.controller.v1;

import com.hqc.security.api.dto.request.CreateUserRequest;
import com.hqc.security.api.dto.response.UserResponse;
import com.hqc.security.common.domain.model.User;
import com.hqc.security.common.domain.port.in.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody @Valid CreateUserRequest request) {
        User user = userService.createUser(
                request.username(),
                request.email(),
                request.password(), 
                request.role()
        );
        return UserResponse.fromDomain(user);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return UserResponse.fromDomain(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
