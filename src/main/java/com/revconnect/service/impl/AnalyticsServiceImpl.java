package com.revconnect.service.impl;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostAnalytics;
import com.revconnect.repository.PostAnalyticsRepository;
import com.revconnect.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.revconnect.dto.response.ProfileAnalyticsResponse;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final PostAnalyticsRepository postAnalyticsRepository;

    private PostAnalytics getOrCreate(Post post){

        return postAnalyticsRepository.findByPost(post)
                .orElseGet(() -> {

                    PostAnalytics analytics = PostAnalytics.builder()
                            .post(post)
                            .totalLikes(0L)
                            .totalComments(0L)
                            .totalShares(0L)
                            .reachCount(0L)
                            .build();

                    return postAnalyticsRepository.save(analytics);
                });
    }

    @Override
    public void incrementLikes(Post post){

        PostAnalytics analytics = getOrCreate(post);

        analytics.setTotalLikes(
                analytics.getTotalLikes() + 1
        );

        postAnalyticsRepository.save(analytics);
    }

    @Override
    public void incrementComments(Post post){

        PostAnalytics analytics = getOrCreate(post);

        analytics.setTotalComments(
                analytics.getTotalComments() + 1
        );

        postAnalyticsRepository.save(analytics);
    }

    @Override
    public void incrementShares(Post post){

        PostAnalytics analytics = getOrCreate(post);

        analytics.setTotalShares(
                analytics.getTotalShares() + 1
        );

        postAnalyticsRepository.save(analytics);
    }

    @Override
    public void decrementLikes(Post post) {

        PostAnalytics analytics = getOrCreate(post);

        if (analytics.getTotalLikes() > 0) {
            analytics.setTotalLikes(
                    analytics.getTotalLikes() - 1
            );
        }

        postAnalyticsRepository.save(analytics);
    }

    @Override
    public void decrementComments(Post post){

        PostAnalytics analytics = getOrCreate(post);

        if (analytics.getTotalComments() > 0) {
            analytics.setTotalComments(
                    analytics.getTotalComments() - 1
            );
        }

        postAnalyticsRepository.save(analytics);
    }

    @Override
    public void decrementShares(Post post){

        PostAnalytics analytics = getOrCreate(post);

        if (analytics.getTotalShares() > 0) {
            analytics.setTotalShares(
                    analytics.getTotalShares() - 1
            );
        }

        postAnalyticsRepository.save(analytics);
    }


    @Override
    public ProfileAnalyticsResponse getUserAnalytics(Long userId) {
        // 1. Receive as a List
        java.util.List<Object[]> results = postAnalyticsRepository.getUserAnalytics(userId);
        ProfileAnalyticsResponse analytics = new ProfileAnalyticsResponse();

        // 2. Check if the list has data
        if (results != null && !results.isEmpty()) {
            Object[] row = results.get(0); // Get the first (and only) row

            // 3. Extract using the safe (Number) casting
            analytics.setTotalLikes(row[0] != null ? ((Number) row[0]).longValue() : 0L);
            analytics.setTotalComments(row[1] != null ? ((Number) row[1]).longValue() : 0L);
            analytics.setTotalShares(row[2] != null ? ((Number) row[2]).longValue() : 0L);
        } else {
            analytics.setTotalLikes(0L);
            analytics.setTotalComments(0L);
            analytics.setTotalShares(0L);
        }
        return analytics;
    }
}