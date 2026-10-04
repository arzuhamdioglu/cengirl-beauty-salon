package com.cengirl.beautysalon.presentation.util;

import com.cengirl.beautysalon.domain.exception.ValidationException;
import com.cengirl.beautysalon.infrastructure.util.DateTimeUtil;
import com.cengirl.beautysalon.infrastructure.util.InputValidator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Shared console input helpers for presentation layer.
 */
public final class ConsoleIO {

    private static final Scanner SCANNER = new Scanner(System.in);

    private ConsoleIO() {
    }

    public static String readLine(String prompt) {
        ConsoleUI.printPrompt(prompt);
        return SCANNER.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                if (prompt == null || prompt.isBlank()) {
                    ConsoleUI.printChoicePrompt();
                } else {
                    ConsoleUI.printPrompt(prompt);
                }
                return Integer.parseInt(SCANNER.nextLine().trim());
            } catch (NumberFormatException ex) {
                ConsoleUI.printWarning("Please enter a valid number.");
            }
        }
    }

    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                return new BigDecimal(readLine(prompt));
            } catch (NumberFormatException ex) {
                ConsoleUI.printWarning("Please enter a valid amount.");
            }
        }
    }

    public static void pressEnterToContinue() {
        ConsoleUI.printPrompt("(press Enter to continue)");
        SCANNER.nextLine();
        ConsoleUI.blankLine();
    }

    public static String readEmail(boolean required) {
        while (true) {
            String value = readLine(required ? "Email (e.g. name@gmail.com)"
                    : "Email (blank = keep, e.g. name@gmail.com)");
            if (!required && value.isBlank()) {
                return value;
            }
            try {
                InputValidator.validateEmail(value);
                return value.trim();
            } catch (ValidationException ex) {
                ConsoleUI.printWarning(ex.getMessage());
            }
        }
    }

    public static String readPhone(boolean required) {
        while (true) {
            String value = readLine(required ? "Phone (05XX XXX XX XX)"
                    : "Phone (blank = keep, 05XX XXX XX XX)");
            if (!required && value.isBlank()) {
                return value;
            }
            try {
                return InputValidator.validateAndNormalizePhone(value);
            } catch (ValidationException ex) {
                ConsoleUI.printWarning(ex.getMessage());
            }
        }
    }

    /**
     * Shows the next 7 bookable days (today inclusive) and returns the selected date.
     */
    public static LocalDate readBookableDateSelection() {
        while (true) {
            List<LocalDate> dates = DateTimeUtil.getBookableDates();
            ConsoleUI.printSection("Available Dates (7 days from today)");
            ConsoleUI.printTableHeader("#", "Date", "Day");
            for (int i = 0; i < dates.size(); i++) {
                LocalDate date = dates.get(i);
                String dayLabel = i == 0 ? "Today"
                        : date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                ConsoleUI.printRow(String.valueOf(i), date.format(DateTimeUtil.DATE_FORMAT), dayLabel);
            }
            int index = readInt("Select date index");
            try {
                return DateTimeUtil.getBookableDateByIndex(index);
            } catch (ValidationException ex) {
                ConsoleUI.printWarning(ex.getMessage());
            }
        }
    }
}
