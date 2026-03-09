package com.revconnect.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class BusinessHoursTest {

    private void invokeValidation(BusinessHours hours) throws Exception {
        Method method = BusinessHours.class.getDeclaredMethod("validateHours");
        method.setAccessible(true);
        method.invoke(hours);
    }

    @Test
    void shouldPassValidationWhenTimesAreCorrect() throws Exception {

        UserProfile profile = new UserProfile();
        profile.setProfileId(1L);

        BusinessHours hours = BusinessHours.builder()
                .profile(profile)
                .dayOfWeek("MONDAY")
                .openTime(LocalTime.of(9,0))
                .closeTime(LocalTime.of(17,0))
                .isClosed(false)
                .build();

        assertDoesNotThrow(() -> invokeValidation(hours));
    }

    @Test
    void shouldThrowExceptionWhenTimesMissing() {

        UserProfile profile = new UserProfile();
        profile.setProfileId(1L);

        BusinessHours hours = BusinessHours.builder()
                .profile(profile)
                .dayOfWeek("TUESDAY")
                .isClosed(false)
                .build();

        assertThrows(Exception.class, () -> invokeValidation(hours));
    }

    @Test
    void shouldThrowExceptionWhenCloseBeforeOpen() {

        UserProfile profile = new UserProfile();
        profile.setProfileId(1L);

        BusinessHours hours = BusinessHours.builder()
                .profile(profile)
                .dayOfWeek("WEDNESDAY")
                .openTime(LocalTime.of(18,0))
                .closeTime(LocalTime.of(9,0))
                .isClosed(false)
                .build();

        assertThrows(Exception.class, () -> invokeValidation(hours));
    }

    @Test
    void shouldAllowClosedBusinessWithoutTimes() throws Exception {

        UserProfile profile = new UserProfile();
        profile.setProfileId(1L);

        BusinessHours hours = BusinessHours.builder()
                .profile(profile)
                .dayOfWeek("SUNDAY")
                .isClosed(true)
                .build();

        assertDoesNotThrow(() -> invokeValidation(hours));
    }
}