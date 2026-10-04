package com.cengirl.beautysalon.application.service;

import com.cengirl.beautysalon.domain.exception.AuthenticationException;
import com.cengirl.beautysalon.infrastructure.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthServiceTest {

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(new InMemoryUserRepository());
        authService.registerCustomer("user1", "pass12", "Test User", "testuser@gmail.com", "0555 555 55 55");
    }

    @Test
    void shouldLockAccountAfterFailedAttempts() {
        assertThrows(AuthenticationException.class, () -> authService.login("user1", "wrong"));
        assertThrows(AuthenticationException.class, () -> authService.login("user1", "wrong"));
        assertThrows(AuthenticationException.class, () -> authService.login("user1", "wrong"));
        assertThrows(AuthenticationException.class, () -> authService.login("user1", "pass12"));
    }
}
