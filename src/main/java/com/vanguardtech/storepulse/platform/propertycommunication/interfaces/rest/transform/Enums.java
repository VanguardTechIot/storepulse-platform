package com.vanguardtech.storepulse.platform.propertycommunication.interfaces.rest.transform;

/** Parses the enum names the web application sends; an unknown value is a 400. */
final class Enums {
    private Enums() {
    }

    static <T extends Enum<T>> T parse(Class<T> type, String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(type.getSimpleName() + " is required");
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown " + type.getSimpleName() + ": " + value);
        }
    }

    static <T extends Enum<T>> T parseOrDefault(Class<T> type, String value, T fallback) {
        return value == null || value.isBlank() ? fallback : parse(type, value);
    }
}
