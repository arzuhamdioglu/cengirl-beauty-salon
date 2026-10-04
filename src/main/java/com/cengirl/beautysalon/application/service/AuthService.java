package com.cengirl.beautysalon.application.service;

import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.exception.AuthenticationException;
import com.cengirl.beautysalon.domain.exception.ValidationException;
import com.cengirl.beautysalon.domain.factory.UserFactory;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.domain.repository.UserRepository;
import com.cengirl.beautysalon.infrastructure.util.IdGenerator;
import com.cengirl.beautysalon.infrastructure.util.InputValidator;

/**
 * Handles registration, login, and account lockout policy.
 */
public class AuthService {

    public static final int LOCK_THRESHOLD = 3;

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerCustomer(String username, String password, String fullName,
                                 String email, String phone) {
        return register(UserRole.CUSTOMER, username, password, fullName, email, phone, null, null);
    }

    public User register(UserRole role, String username, String password, String fullName,
                         String email, String phone,
                         com.cengirl.beautysalon.domain.valueobject.WorkSchedule schedule,
                         java.math.BigDecimal salary) {
        if (userRepository.existsByUsername(username)) {
            throw new ValidationException("Username already exists.");
        }
        InputValidator.validateEmail(email);
        String normalizedPhone = InputValidator.validateAndNormalizePhone(phone);
        User user = UserFactory.create(role, IdGenerator.next("USR"), username, password,
                fullName, email.trim(), normalizedPhone, schedule, salary);
        userRepository.save(user);
        return user;
    }

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        if (user.isLocked()) {
            throw new AuthenticationException("Account locked due to failed login attempts.");
        }
        if (!user.verifyPassword(password)) {
            user.registerFailedLogin(LOCK_THRESHOLD);
            throw new AuthenticationException("Invalid username or password.");
        }
        user.resetFailedLogins();
        return user;
    }
}
