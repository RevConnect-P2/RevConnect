package com.revconnect.dto.request;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class BusinessHoursRequestTest {

    @Test
    void testGettersAndSetters() {

        BusinessHoursRequest request = new BusinessHoursRequest();

        LocalTime open = LocalTime.of(9, 0);
        LocalTime close = LocalTime.of(18, 0);

        request.setDayOfWeek("Monday");
        request.setOpenTime(open);
        request.setCloseTime(close);
        request.setIsClosed(false);

        assertEquals("Monday", request.getDayOfWeek());
        assertEquals(open, request.getOpenTime());
        assertEquals(close, request.getCloseTime());
        assertFalse(request.getIsClosed());
    }
}