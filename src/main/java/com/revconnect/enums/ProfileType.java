package com.revconnect.enums;

/**
 * ProfileType Enum
 * Defines the type of profile in RevConnect
 *
 * PERSONAL  - Normal individual user (default)
 * CREATOR   - Content creator
 * BUSINESS  - Business profile
 */
public enum ProfileType {

    PERSONAL,
    CREATOR,
    BUSINESS;

    /**
     * Convert String to ProfileType safely
     */
    public static ProfileType fromString(String value) {

        if (value == null || value.isBlank()) {
            return PERSONAL;
        }

        try {
            return ProfileType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {

            // Default fallback
            return PERSONAL;

        }

    }

}