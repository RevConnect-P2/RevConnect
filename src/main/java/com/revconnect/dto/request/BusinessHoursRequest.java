package com.revconnect.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BusinessHoursRequest {

    private String dayOfWeek;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Boolean isClosed;
}