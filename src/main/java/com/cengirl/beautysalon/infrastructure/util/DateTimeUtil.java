package com.cengirl.beautysalon.infrastructure.util;

import com.cengirl.beautysalon.domain.exception.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parses and formats date-time values for console input.
 */
public final class DateTimeUtil {

    /** Number of bookable days starting from today (today inclusive). */
    public static final int APPOINTMENT_BOOKING_DAYS = 7;

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DateTimeUtil() {
    }

    public static LocalDate parseDate(String input) {
        try {
            return LocalDate.parse(input.trim(), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Invalid date. Use yyyy-MM-dd.");
        }
    }

    /**
     * Returns bookable dates: today and the following 6 days (7 days total).
     */
    public static List<LocalDate> getBookableDates() {
        LocalDate today = LocalDate.now();
        List<LocalDate> dates = new ArrayList<>(APPOINTMENT_BOOKING_DAYS);
        for (int i = 0; i < APPOINTMENT_BOOKING_DAYS; i++) {
            dates.add(today.plusDays(i));
        }
        return Collections.unmodifiableList(dates);
    }

    public static LocalDate getBookableDateByIndex(int index) {
        List<LocalDate> dates = getBookableDates();
        if (index < 0 || index >= dates.size()) {
            throw new ValidationException("Invalid date index. Choose 0 to " + (dates.size() - 1) + ".");
        }
        return dates.get(index);
    }

    /**
     * Validates that a date is within the 7-day booking window (today inclusive).
     */
    public static void validateBookableDate(LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate lastAllowed = today.plusDays(APPOINTMENT_BOOKING_DAYS - 1);
        if (date.isBefore(today) || date.isAfter(lastAllowed)) {
            throw new ValidationException("Date must be within booking window: "
                    + formatRange(today, lastAllowed));
        }
    }

    public static String appointmentDateRangeHint() {
        List<LocalDate> dates = getBookableDates();
        return formatRange(dates.get(0), dates.get(dates.size() - 1));
    }

    private static String formatRange(LocalDate from, LocalDate to) {
        return from.format(DATE_FORMAT) + " to " + to.format(DATE_FORMAT);
    }

    public static LocalDateTime parseDateTime(String input) {
        try {
            return LocalDateTime.parse(input.trim(), DATE_TIME_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new ValidationException("Invalid date-time. Use yyyy-MM-dd HH:mm.");
        }
    }
}
