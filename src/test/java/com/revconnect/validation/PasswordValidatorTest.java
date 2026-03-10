package com.revconnect.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PasswordValidatorTest {

    @Test
    void testConstructor() {

        PasswordValidator validator = new PasswordValidator();

        assertNotNull(validator);
    }
}