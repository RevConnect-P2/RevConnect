package com.revconnect.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmailValidatorTest {

    @Test
    void testConstructor() {

        EmailValidator validator = new EmailValidator();

        assertNotNull(validator);
    }
}