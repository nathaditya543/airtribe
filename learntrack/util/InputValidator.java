package util;

import exceptions.InvalidInputException;

/**
 * Small collection of static helpers for validating console input.
 * Keeps Main focused on menu flow instead of validation logic.
 */
public class InputValidator {

    private InputValidator() { }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    public static void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new InvalidInputException(fieldName + " must be a positive number.");
        }
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}
