package com.revconnect.dto.response;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class BusinessHoursResponseTest {

    @Test
    void testGettersAndSetters() {

        BusinessHoursResponse response = new BusinessHoursResponse();

        LocalTime open = LocalTime.of(9, 0);
        LocalTime close = LocalTime.of(18, 0);

        response.setDayOfWeek("Monday");
        response.setOpenTime(open);
        response.setCloseTime(close);
        response.setIsClosed(false);

        assertEquals("Monday", response.getDayOfWeek());
        assertEquals(open, response.getOpenTime());
        assertEquals(close, response.getCloseTime());
        assertFalse(response.getIsClosed());
    }
}