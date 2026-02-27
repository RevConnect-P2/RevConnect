package com.revconnect.service.impl;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.*;
import com.revconnect.exception.BadRequestException;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.exception.UnauthorizedException;
import com.revconnect.mapper.PostMapper;
import com.revconnect.repository.*;
import com.revconnect.service.PostService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final PostMapper postMapper;

    @Override
    public PostResponse createPost(Long userId, PostCreateRequest request) {

        // 1️⃣ Validate content
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BadRequestException("Post content cannot be empty");
        }

        // 2️⃣ Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3️⃣ Validate post type (string-based for now)
        String postType = request.getPostType();
        if (postType == null ||
                (!postType.equalsIgnoreCase("NORMAL") &&
                        !postType.equalsIgnoreCase("PROMOTIONAL"))) {
            throw new BadRequestException("Invalid post type");
        }

        // 4️⃣ Validate CTA for promotional posts
        if (postType.equalsIgnoreCase("PROMOTIONAL")) {
            if (request.getCtaText() == null || request.getCtaLink() == null) {
                throw new BadRequestException("CTA text and link are required for promotional posts");
            }
        }

        // 5️⃣ Create Post entity
        Post post = Post.builder()
                .user(user)
                .content(request.getContent())
                .postType(postType.toUpperCase())
                .pinned(request.getPinned() != null ? request.getPinned() : false)
                .ctaText(request.getCtaText())
                .ctaLink(request.getCtaLink())
                .scheduledAt(request.getScheduledAt())
                .build();

        Post savedPost = postRepository.save(post);

        // 6️⃣ Handle hashtags
        List<String> hashtagNames = request.getHashtags();
        List<Hashtag> hashtags = new ArrayList<>();

        if (hashtagNames != null && !hashtagNames.isEmpty()) {

            for (String tag : hashtagNames) {
                if (tag == null || tag.trim().isEmpty()) continue;

                String normalizedTag = tag.trim().toLowerCase();

                Hashtag hashtag = hashtagRepository
                        .findByTagName(normalizedTag)
                        .orElseGet(() -> hashtagRepository.save(
                                Hashtag.builder()
                                        .tagName(normalizedTag)
                                        .build()
                        ));

                PostHashtag postHashtag = PostHashtag.builder()
                        .post(savedPost)
                        .hashtag(hashtag)
                        .build();

                postHashtagRepository.save(postHashtag);
                hashtags.add(hashtag);
            }
        }

        // 7️⃣ Map to response
        return postMapper.toPostResponse(savedPost, hashtags);
    }

    @Transactional
    @Override
    public PostResponse updatePost(Long postId, Long userId, PostCreateRequest request) {

        // 1️⃣ Validate content
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BadRequestException("Post content cannot be empty");
        }

        // 2️⃣ Fetch post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // 3️⃣ Ownership check
        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not allowed to update this post");
        }

        // 4️⃣ Validate post type
        String postType = request.getPostType();
        if (postType == null ||
                (!postType.equalsIgnoreCase("NORMAL") &&
                        !postType.equalsIgnoreCase("PROMOTIONAL"))) {
            throw new BadRequestException("Invalid post type");
        }

        // 5️⃣ Validate CTA for promotional posts
        if (postType.equalsIgnoreCase("PROMOTIONAL")) {
            if (request.getCtaText() == null || request.getCtaLink() == null) {
                throw new BadRequestException("CTA text and link are required for promotional posts");
            }
        }

        // 6️⃣ Update post fields
        post.setContent(request.getContent());
        post.setPostType(postType.toUpperCase());
        post.setPinned(request.getPinned() != null ? request.getPinned() : false);
        post.setCtaText(request.getCtaText());
        post.setCtaLink(request.getCtaLink());
        post.setScheduledAt(request.getScheduledAt());

        Post updatedPost = postRepository.save(post);

        // 7️⃣ Remove old hashtag mappings
        postHashtagRepository.deleteByPost(updatedPost);

        // 8️⃣ Add new hashtags
        List<String> hashtagNames = request.getHashtags();
        List<Hashtag> hashtags = new ArrayList<>();

        if (hashtagNames != null && !hashtagNames.isEmpty()) {

            for (String tag : hashtagNames) {
                if (tag == null || tag.trim().isEmpty()) continue;

                String normalizedTag = tag.trim().toLowerCase();

                Hashtag hashtag = hashtagRepository
                        .findByTagName(normalizedTag)
                        .orElseGet(() -> hashtagRepository.save(
                                Hashtag.builder()
                                        .tagName(normalizedTag)
                                        .build()
                        ));

                PostHashtag postHashtag = PostHashtag.builder()
                        .post(updatedPost)
                        .hashtag(hashtag)
                        .build();

                postHashtagRepository.save(postHashtag);
                hashtags.add(hashtag);
            }
        }

        // 9️⃣ Return response
        return postMapper.toPostResponse(updatedPost, hashtags);
    }

    @Transactional
    @Override
    public void deletePost(Long postId, Long userId) {

        // 1️⃣ Fetch post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // 2️⃣ Ownership check
        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not allowed to delete this post");
        }

        // 3️⃣ Remove hashtag mappings
        postHashtagRepository.deleteByPost(post);

        // 4️⃣ Delete post
        postRepository.delete(post);
    }

    @Override
    public List<PostResponse> getPostsByUser(Long userId) {

        // 1️⃣ Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2️⃣ Fetch posts by user
        List<Post> posts = postRepository.findAll()
                .stream()
                .filter(post -> post.getUser().getUserId().equals(user.getUserId()))
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .toList();

        // 3️⃣ Map posts to response
        List<PostResponse> responses = new ArrayList<>();

        for (Post post : posts) {
            List<Hashtag> hashtags = postHashtagRepository.findAll()
                    .stream()
                    .filter(ph -> ph.getPost().getPostId().equals(post.getPostId()))
                    .map(PostHashtag::getHashtag)
                    .toList();

            responses.add(postMapper.toPostResponse(post, hashtags));
        }

        return responses;
    }

    @Override
    public PostResponse getPostById(Long postId) {

        // 1️⃣ Fetch post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // 2️⃣ Fetch hashtags
        List<Hashtag> hashtags = postHashtagRepository.findAll()
                .stream()
                .filter(ph -> ph.getPost().getPostId().equals(post.getPostId()))
                .map(PostHashtag::getHashtag)
                .toList();

        // 3️⃣ Map to response
        return postMapper.toPostResponse(post, hashtags);
    }
    // Other methods will be implemented next
}