package com.revconnect.service.impl;

import com.revconnect.common.PageResponse;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.Post;
import com.revconnect.mapper.PostMapper;
import com.revconnect.repository.PostRepository;
import com.revconnect.service.FeedService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class FeedServiceImpl implements FeedService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Override
    public PageResponse<PostResponse> getPersonalizedFeed(
            Long userId,
            int page,
            int size,
            String postType,
            String userType) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );

        Page<Post> postPage =
                postRepository.getPersonalizedFeed(userId, pageable);

        return new PageResponse<>(
                postPage.getContent()
                        .stream()
                        .map(postMapper::toResponse)
                        .toList(),
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements()
        );
    }

    @Override
    public PageResponse<PostResponse> getTrendingPosts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> trendingPosts =
                postRepository.findTrendingPosts(pageable);

        return new PageResponse<>(
                trendingPosts.getContent()
                        .stream()
                        .map(postMapper::toResponse)
                        .toList(),
                trendingPosts.getNumber(),
                trendingPosts.getSize(),
                trendingPosts.getTotalElements()
        );
    }

    @Override
    public PageResponse<PostResponse> searchByHashtag(
            String hashtag,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> postPage =
                postRepository.findByHashtag(hashtag, pageable);

        return new PageResponse<>(
                postPage.getContent()
                        .stream()
                        .map(postMapper::toResponse)
                        .toList(),
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalElements()
        );
    }
}