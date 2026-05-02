package stmarys.library.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    public static int positiveInt(String value, String fieldName) throws ValidationException {
        try {
            int number = Integer.parseInt(requiredText(value, fieldName));
            if (number <= 0) {
                throw new ValidationException(fieldName + " must be greater than zero.");
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new ValidationException(fieldName + " must be numeric.");
        }
    }

    public static String requiredText(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " must not be empty.");
        }
        return value.trim();
    }

    public static String optionalText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "";
        }
        return value.trim();
    }

    public static String email(String value) throws ValidationException {
        String email = requiredText(value, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Email address is not valid.");
        }
        return email;
    }

    public static LocalDate date(String value, String fieldName) throws ValidationException {
        try {
            return LocalDate.parse(requiredText(value, fieldName));
        } catch (DateTimeParseException ex) {
            throw new ValidationException(fieldName + " must use the format yyyy-mm-dd.");
        }
    }

    public static String oneOf(String value, String fieldName, String... allowed) throws ValidationException {
        String text = requiredText(value, fieldName);
        for (String option : allowed) {
            if (option.equalsIgnoreCase(text)) {
                return option;
            }
        }
        throw new ValidationException(fieldName + " must be one of the allowed values.");
    }
}
