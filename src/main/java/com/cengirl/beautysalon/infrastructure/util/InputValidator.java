package com.cengirl.beautysalon.infrastructure.util;

import com.cengirl.beautysalon.domain.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Validates and normalizes user input such as email and phone numbers.
 */
public final class InputValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(0\\s?5\\d{2}[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2}|5\\d{2}[\\s-]?\\d{3}[\\s-]?\\d{2}[\\s-]?\\d{2})$");

    private InputValidator() {
    }

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("Email is required.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format. Example: name@gmail.com");
        }
    }

    public static void validateEmailIfPresent(String email) {
        if (email != null && !email.isBlank()) {
            validateEmail(email);
        }
    }

    public static String validateAndNormalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new ValidationException("Phone number is required.");
        }
        String trimmed = phone.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new ValidationException("Invalid phone format. Use: 05XX XXX XX XX (e.g. 0532 123 45 67)");
        }
        String digits = trimmed.replaceAll("\\D", "");
        if (digits.length() == 10 && digits.startsWith("5")) {
            digits = "0" + digits;
        }
        return String.format("%s %s %s %s",
                digits.substring(0, 4),
                digits.substring(4, 7),
                digits.substring(7, 9),
                digits.substring(9, 11));
    }

    public static String validateAndNormalizePhoneIfPresent(String phone) {
        if (phone == null || phone.isBlank()) {
            return phone;
        }
        return validateAndNormalizePhone(phone);
    }
}
