package dev.lars.apimanager.database;

import java.util.regex.Pattern;

public class SqlIdentifierValidator {

    private static final Pattern SAFE_IDENTIFIER = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]{0,63}$");

    public static String validate(String identifier) {
        if (identifier == null || !SAFE_IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException(
                "Unsafe SQL identifier rejected: '" + identifier + "'"
            );
        }
        return identifier;
    }

    public static String[] validateAll(String[] identifiers) {
        if (identifiers == null) {
            throw new IllegalArgumentException("Identifier array cannot be null");
        }
        String[] result = new String[identifiers.length];
        for (int i = 0; i < identifiers.length; i++) {
            result[i] = validate(identifiers[i]);
        }
        return result;
    }
}