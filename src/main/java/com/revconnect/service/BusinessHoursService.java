package com.revconnect.service;

import com.revconnect.dto.response.BusinessHoursResponse;

import java.util.List;

public interface BusinessHoursService {

    List<BusinessHoursResponse> getBusinessHours(Long userId);
}