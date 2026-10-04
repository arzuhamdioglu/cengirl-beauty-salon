package com.cengirl.beautysalon.domain.model;

import com.cengirl.beautysalon.domain.enums.UserRole;
import com.cengirl.beautysalon.domain.exception.ValidationException;

import java.util.Objects;

/**
 * Abstract base for all system actors. Encapsulates credentials and profile data.
 */
public abstract class User {

    private final String id;
    private final UserRole role;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private int failedLoginAttempts;
    private boolean locked;

    protected User(String id, UserRole role, String username, String password,
                   String fullName, String email, String phone) {
        validateCredentials(username, password);
        this.id = Objects.requireNonNull(id);
        this.role = Objects.requireNonNull(role);
        this.username = username.trim();
        this.password = password;
        this.fullName = fullName == null ? "" : fullName.trim();
        this.email = email == null ? "" : email.trim();
        this.phone = phone == null ? "" : phone.trim();
    }

    public String getId() {
        return id;
    }

    public UserRole getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public boolean isLocked() {
        return locked;
    }

    public void updateProfile(String fullName, String email, String phone) {
        if (fullName != null && !fullName.isBlank()) {
            this.fullName = fullName.trim();
        }
        if (email != null && !email.isBlank()) {
            this.email = email.trim();
        }
        if (phone != null) {
            this.phone = phone.trim();
        }
    }

    public boolean verifyPassword(String candidate) {
        return password.equals(candidate);
    }

    public void registerFailedLogin(int lockThreshold) {
        failedLoginAttempts++;
        if (failedLoginAttempts >= lockThreshold) {
            locked = true;
        }
    }

    public void resetFailedLogins() {
        failedLoginAttempts = 0;
        locked = false;
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 4) {
            throw new ValidationException("Password must be at least 4 characters.");
        }
        this.password = newPassword;
    }

    private static void validateCredentials(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new ValidationException("Username is required.");
        }
        if (password == null || password.length() < 4) {
            throw new ValidationException("Password must be at least 4 characters.");
        }
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
