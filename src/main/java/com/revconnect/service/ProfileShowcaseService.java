package com.revconnect.service;

import com.revconnect.dto.request.ShowcaseRequest;
import com.revconnect.dto.response.ShowcaseResponse;

import java.util.List;

public interface ProfileShowcaseService {

    void addShowcase(Long userId, ShowcaseRequest request);

    List<ShowcaseResponse> getShowcases(Long userId);

    void updateShowcase(Long userId, Long showcaseId, ShowcaseRequest request);

    void deleteShowcase(Long userId, Long showcaseId);
}