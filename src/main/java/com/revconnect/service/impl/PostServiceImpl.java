package com.revconnect.service.impl;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.request.TagRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.dto.response.TagResponse;
import com.revconnect.entity.*;
import com.revconnect.enums.NotificationType;
import com.revconnect.exception.BadRequestException;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.exception.UnauthorizedException;
import com.revconnect.mapper.PostMapper;
import com.revconnect.repository.*;
import com.revconnect.service.NotificationService;
import com.revconnect.service.PostService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final PostTagRepository postTagRepository;
    private final PostMapper postMapper;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final ShareRepository shareRepository;

    private final NotificationService notificationService;
    private final FollowRepository followRepository;

    // =========================
    // CREATE POST
    // =========================
    @Override
    @Transactional
    public PostResponse createPost(Long userId, PostCreateRequest request) {

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BadRequestException("Post content cannot be empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String postType = request.getPostType();

        if (postType == null ||
                (!postType.equalsIgnoreCase("NORMAL")
                        && !postType.equalsIgnoreCase("PROMOTIONAL"))) {
            throw new BadRequestException("Invalid post type");
        }

        if (postType.equalsIgnoreCase("PROMOTIONAL")) {
            if (request.getCtaText() == null || request.getCtaLink() == null) {
                throw new BadRequestException("CTA text and link are required");
            }
        }

        Post post = Post.builder()
                .user(user)
                .content(request.getContent())
                .postType(postType.toUpperCase())
                .pinned(Boolean.TRUE.equals(request.getPinned()))
                .ctaText(request.getCtaText())
                .ctaLink(request.getCtaLink())
                .scheduledAt(request.getScheduledAt())
                .build();

        Post savedPost = postRepository.save(post);

        List<Hashtag> hashtags = saveHashtags(savedPost, request.getHashtags());
        List<TagResponse> tags = saveTags(savedPost, request.getTags());

        notifyFollowers(userId, savedPost.getPostId());

        return mapPost(savedPost, hashtags, tags);
    }

    // =========================
    // UPDATE POST
    // =========================
    @Override
    @Transactional
    public PostResponse updatePost(Long postId, Long userId, PostCreateRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("Not allowed");
        }

        post.setContent(request.getContent());
        post.setPostType(request.getPostType().toUpperCase());
        post.setPinned(request.getPinned());
        post.setCtaText(request.getCtaText());
        post.setCtaLink(request.getCtaLink());
        post.setScheduledAt(request.getScheduledAt());

        Post updatedPost = postRepository.save(post);

        postHashtagRepository.deleteByPost(updatedPost);
        postTagRepository.deleteByPost(updatedPost);

        List<Hashtag> hashtags = saveHashtags(updatedPost, request.getHashtags());
        List<TagResponse> tags = saveTags(updatedPost, request.getTags());

        return mapPost(updatedPost, hashtags, tags);
    }

    // =========================
    // DELETE POST
    // =========================
    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("Not allowed");
        }

        shareRepository.deleteByOriginalPost(post);
        postTagRepository.deleteByPost(post);
        postHashtagRepository.deleteByPost(post);

        postRepository.delete(post);
    }

    // =========================
    // GET POSTS BY USER
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Post> posts =
                postRepository.findVisiblePostsByUser(user, LocalDateTime.now());

        return buildPostResponses(posts);
    }

    // =========================
    // GET POST BY ID
    // =========================
    @Override
    public PostResponse getPostById(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return mapPost(post, getHashtagsForPost(post), getTagsForPost(post));
    }

    // =========================
    // GLOBAL FEED
    // =========================
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getGlobalFeed(Long viewerUserId) {

        List<Post> posts =
                postRepository.findGlobalFeedPosts(viewerUserId, LocalDateTime.now());

        List<PostResponse> responses = buildPostResponses(posts);

        // ===============================
        // LOAD SHARED POSTS
        // ===============================
        List<Share> shares = shareRepository.findAllByOrderByCreatedAtDesc();

        if (shares != null) {
            for (Share share : shares) {

                if (share == null || share.getOriginalPost() == null) {
                    continue;
                }

                Post original = share.getOriginalPost();

                PostResponse response = mapPost(
                        original,
                        getHashtagsForPost(original),
                        getTagsForPost(original)
                );

                // 🔁 MARK AS SHARED
                response.setSharedPost(true);

                // IMPORTANT: Use SHARE TIME
                response.setCreatedAt(share.getCreatedAt());

                if (share.getSharedBy() != null) {
                    response.setSharedByUsername(
                            share.getSharedBy().getUsername()
                    );
                }

                if (original.getUser() != null) {
                    response.setOriginalAuthorUsername(
                            original.getUser().getUsername()
                    );
                }

                responses.add(response);
            }
        }

        // ===============================
        // SORT FEED
        // ===============================
        responses.sort(
                Comparator.comparing(
                        PostResponse::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).reversed()
        );

        return responses;
    }
    // =========================
    // PIN POST
    // =========================
    @Override
    @Transactional
    public PostResponse pinPost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("Cannot pin this post");
        }

        postRepository.findByUserAndPinnedTrue(post.getUser())
                .ifPresent(existing -> {
                    existing.setPinned(false);
                    postRepository.save(existing);
                });

        post.setPinned(true);

        return mapPost(postRepository.save(post),
                getHashtagsForPost(post),
                getTagsForPost(post));
    }

    // =========================
    // UNPIN POST
    // =========================
    @Override
    @Transactional
    public PostResponse unpinPost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        post.setPinned(false);

        return mapPost(postRepository.save(post),
                getHashtagsForPost(post),
                getTagsForPost(post));
    }

    // =========================
    // COUNT POSTS
    // =========================
    @Override
    public long countPostsByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return postRepository.countByUser(user);
    }

    // =========================
    // TRENDING HASHTAGS
    // =========================
    @Override
    public List<String> getTrendingHashtags() {

        return postRepository.findTrendingHashtags(PageRequest.of(0, 5));
    }

    // =========================
    // POSTS BY HASHTAG
    // =========================
    @Override
    public List<PostResponse> getPostsByHashtag(String hashtag) {

        List<Post> posts = postRepository.findPostsByHashtag(hashtag);

        return buildPostResponses(posts);
    }

    // =========================
    // PRIVATE HELPERS
    // =========================

    private void notifyFollowers(Long userId, Long postId) {

        followRepository.findByFollowing_UserId(userId)
                .stream()
                .map(Follow::getFollower)
                .filter(follower -> !follower.getUserId().equals(userId))
                .forEach(follower -> notificationService.createNotification(
                        userId,
                        follower.getUserId(),
                        postId,
                        NotificationType.POST,
                        null
                ));
    }

    private List<PostResponse> buildPostResponses(List<Post> posts) {

        List<PostResponse> responses = new ArrayList<>();

        for (Post post : posts) {

            responses.add(
                    mapPost(post,
                            getHashtagsForPost(post),
                            getTagsForPost(post))
            );
        }

        return responses;
    }

    private PostResponse mapPost(Post post,
                                 List<Hashtag> hashtags,
                                 List<TagResponse> tags) {

        PostResponse response = postMapper.toPostResponse(post, hashtags, tags);

        response.setLikeCount(
                postLikeRepository.countByPost_PostId(post.getPostId())
        );

        response.setCommentCount(
                commentRepository.countByPost_PostId(post.getPostId())
        );

        response.setShareCount(
                shareRepository.countByOriginalPost_PostId(post.getPostId())
        );

        return response;
    }

    private List<Hashtag> saveHashtags(Post post, List<String> tags) {

        List<Hashtag> hashtags = new ArrayList<>();

        if (tags == null) return hashtags;

        for (String tag : tags) {

            String normalized = tag.trim().toLowerCase();

            Hashtag hashtag =
                    hashtagRepository.findByTagName(normalized)
                            .orElseGet(() ->
                                    hashtagRepository.save(
                                            Hashtag.builder()
                                                    .tagName(normalized)
                                                    .build()
                                    ));

            postHashtagRepository.save(
                    PostHashtag.builder()
                            .post(post)
                            .hashtag(hashtag)
                            .build()
            );

            hashtags.add(hashtag);
        }

        return hashtags;
    }

    private List<TagResponse> saveTags(Post post, List<TagRequest> tags) {

        List<TagResponse> responses = new ArrayList<>();

        if (tags == null) return responses;

        for (TagRequest tag : tags) {

            PostTag postTag = PostTag.builder()
                    .post(post)
                    .tagName(tag.getTagName())
                    .tagType(tag.getTagType())
                    .build();

            postTagRepository.save(postTag);

            responses.add(
                    TagResponse.builder()
                            .tagName(tag.getTagName())
                            .tagType(tag.getTagType())
                            .build()
            );
        }

        return responses;
    }

    private List<Hashtag> getHashtagsForPost(Post post) {

        return postHashtagRepository
                .findByPost(post)
                .stream()
                .map(PostHashtag::getHashtag)
                .toList();
    }

    private List<TagResponse> getTagsForPost(Post post) {

        return postTagRepository.findByPost(post)
                .stream()
                .map(tag ->
                        TagResponse.builder()
                                .tagName(tag.getTagName())
                                .tagType(tag.getTagType())
                                .build())
                .toList();
    }
}