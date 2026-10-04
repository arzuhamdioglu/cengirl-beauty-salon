package com.cengirl.beautysalon.application.service;

import com.cengirl.beautysalon.domain.exception.EntityNotFoundException;
import com.cengirl.beautysalon.domain.model.User;
import com.cengirl.beautysalon.domain.repository.UserRepository;
import com.cengirl.beautysalon.infrastructure.util.InputValidator;

public class UserProfileService {

    private final UserRepository userRepository;

    public UserProfileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
    }

    public void updateProfile(String userId, String fullName, String email, String phone) {
        User user = getUser(userId);
        InputValidator.validateEmailIfPresent(email);
        String normalizedPhone = InputValidator.validateAndNormalizePhoneIfPresent(phone);
        String normalizedEmail = email == null || email.isBlank() ? email : email.trim();
        user.updateProfile(fullName, normalizedEmail, normalizedPhone);
    }
}
