package com.revconnect.service.impl;

import com.revconnect.dto.response.ProfileAnalyticsResponse;
import com.revconnect.entity.Post;
import com.revconnect.entity.PostAnalytics;
import com.revconnect.repository.PostAnalyticsRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class AnalyticsServiceImplTest {

    @Mock
    private PostAnalyticsRepository postAnalyticsRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private Post post;
    private PostAnalytics analytics;

    @Before
    public void setup() {

        post = new Post();
        post.setPostId(10L);

        analytics = PostAnalytics.builder()
                .post(post)
                .totalLikes(1L)
                .totalComments(1L)
                .totalShares(1L)
                .reachCount(0L)
                .build();
    }

    // =============================
    // INCREMENT LIKES
    // =============================

    @Test
    public void shouldIncrementLikes() {

        when(postAnalyticsRepository.findByPost(post))
                .thenReturn(Optional.of(analytics));

        analyticsService.incrementLikes(post);

        verify(postAnalyticsRepository).save(analytics);

        assertEquals(Long.valueOf(2), analytics.getTotalLikes());
    }

    // =============================
    // INCREMENT COMMENTS
    // =============================

    @Test
    public void shouldIncrementComments() {

        when(postAnalyticsRepository.findByPost(post))
                .thenReturn(Optional.of(analytics));

        analyticsService.incrementComments(post);

        verify(postAnalyticsRepository).save(analytics);

        assertEquals(Long.valueOf(2), analytics.getTotalComments());
    }

    // =============================
    // INCREMENT SHARES
    // =============================

    @Test
    public void shouldIncrementShares() {

        when(postAnalyticsRepository.findByPost(post))
                .thenReturn(Optional.of(analytics));

        analyticsService.incrementShares(post);

        verify(postAnalyticsRepository).save(analytics);

        assertEquals(Long.valueOf(2), analytics.getTotalShares());
    }

    // =============================
    // CREATE ANALYTICS IF MISSING
    // =============================

    @Test
    public void shouldCreateAnalyticsIfNotExists() {

        when(postAnalyticsRepository.findByPost(post))
                .thenReturn(Optional.empty());

        when(postAnalyticsRepository.save(any()))
                .thenReturn(analytics);

        analyticsService.incrementLikes(post);

        verify(postAnalyticsRepository, times(2))
                .save(any(PostAnalytics.class));
    }

    // =============================
    // DECREMENT LIKES
    // =============================

    @Test
    public void shouldDecrementLikes() {

        when(postAnalyticsRepository.findByPost(post))
                .thenReturn(Optional.of(analytics));

        analyticsService.decrementLikes(post);

        verify(postAnalyticsRepository).save(analytics);

        assertEquals(Long.valueOf(0), analytics.getTotalLikes());
    }

    // =============================
    // DECREMENT COMMENTS
    // =============================

    @Test
    public void shouldDecrementComments() {

        when(postAnalyticsRepository.findByPost(post))
                .thenReturn(Optional.of(analytics));

        analyticsService.decrementComments(post);

        verify(postAnalyticsRepository).save(analytics);

        assertEquals(Long.valueOf(0), analytics.getTotalComments());
    }

    // =============================
    // DECREMENT SHARES
    // =============================

    @Test
    public void shouldDecrementShares() {

        when(postAnalyticsRepository.findByPost(post))
                .thenReturn(Optional.of(analytics));

        analyticsService.decrementShares(post);

        verify(postAnalyticsRepository).save(analytics);

        assertEquals(Long.valueOf(0), analytics.getTotalShares());
    }

    // =============================
    // SHOULD NOT GO BELOW ZERO
    // =============================

    @Test
    public void shouldNotDecrementBelowZero() {

        analytics.setTotalLikes(0L);

        when(postAnalyticsRepository.findByPost(post))
                .thenReturn(Optional.of(analytics));

        analyticsService.decrementLikes(post);

        assertEquals(Long.valueOf(0), analytics.getTotalLikes());
    }

    // =============================
    // GET USER ANALYTICS
    // =============================

    @Test
    public void shouldReturnUserAnalytics() {

        when(postAnalyticsRepository.getUserAnalytics(1L))
                .thenReturn(java.util.Collections.singletonList(new Object[]{5L,3L,2L}));

        ProfileAnalyticsResponse response =
                analyticsService.getUserAnalytics(1L);

        assertEquals(Long.valueOf(5), response.getTotalLikes());
        assertEquals(Long.valueOf(3), response.getTotalComments());
        assertEquals(Long.valueOf(2), response.getTotalShares());
    }

    // =============================
    // EMPTY ANALYTICS RESULT
    // =============================

    @Test
    public void shouldReturnZeroAnalyticsIfEmpty() {

        when(postAnalyticsRepository.getUserAnalytics(1L))
                .thenReturn(List.of());

        ProfileAnalyticsResponse response =
                analyticsService.getUserAnalytics(1L);

        assertEquals(Long.valueOf(0), response.getTotalLikes());
        assertEquals(Long.valueOf(0), response.getTotalComments());
        assertEquals(Long.valueOf(0), response.getTotalShares());
    }
}