package com.revconnect.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BusinessHoursResponse {

    private String dayOfWeek;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Boolean isClosed;
}