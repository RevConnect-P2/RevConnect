package com.revconnect.service.impl;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.request.TagRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.dto.response.TagResponse;
import com.revconnect.entity.*;
import com.revconnect.exception.BadRequestException;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.exception.UnauthorizedException;
import com.revconnect.mapper.PostMapper;
import com.revconnect.repository.*;
import com.revconnect.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final PostTagRepository postTagRepository;
    private final PostMapper postMapper;

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
                throw new BadRequestException("CTA text and link are required for promotional posts");
            }
        }

        Post post = Post.builder()
                .user(user)
                .content(request.getContent())
                .postType(postType.toUpperCase())
                .pinned(request.getPinned() != null && request.getPinned())
                .ctaText(request.getCtaText())
                .ctaLink(request.getCtaLink())
                .scheduledAt(request.getScheduledAt())
                .build();

        Post savedPost = postRepository.save(post);

        // ---------- HASHTAGS ----------
        List<Hashtag> hashtags = new ArrayList<>();
        if (request.getHashtags() != null) {
            for (String tag : request.getHashtags()) {
                if (tag == null || tag.trim().isEmpty()) continue;

                String normalized = tag.trim().toLowerCase();

                Hashtag hashtag = hashtagRepository
                        .findByTagName(normalized)
                        .orElseGet(() ->
                                hashtagRepository.save(
                                        Hashtag.builder().tagName(normalized).build()
                                )
                        );

                postHashtagRepository.save(
                        PostHashtag.builder()
                                .post(savedPost)
                                .hashtag(hashtag)
                                .build()
                );

                hashtags.add(hashtag);
            }
        }

        // ---------- PRODUCT / SERVICE TAGS ----------
        List<TagResponse> tagResponses = new ArrayList<>();

        if (request.getTags() != null) {
            for (TagRequest tagReq : request.getTags()) {

                // ✅ STRONG validation (THIS IS THE FIX)
                if (tagReq == null ||
                        tagReq.getTagName() == null ||
                        tagReq.getTagName().trim().isEmpty() ||
                        tagReq.getTagType() == null) {
                    continue;
                }

                PostTag postTag = PostTag.builder()
                        .post(savedPost)              // or updatedPost in updatePost
                        .tagName(tagReq.getTagName().trim())
                        .tagType(tagReq.getTagType())
                        .build();

                postTagRepository.save(postTag);

                tagResponses.add(
                        TagResponse.builder()
                                .tagName(postTag.getTagName())
                                .tagType(postTag.getTagType())
                                .build()
                );
            }
        }

        return postMapper.toPostResponse(savedPost, hashtags, tagResponses);
    }

    // =========================
    // UPDATE POST
    // =========================
    @Override
    @Transactional
    public PostResponse updatePost(Long postId, Long userId, PostCreateRequest request) {

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BadRequestException("Post content cannot be empty");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not allowed to update this post");
        }

        String postType = request.getPostType();
        if (postType == null ||
                (!postType.equalsIgnoreCase("NORMAL")
                        && !postType.equalsIgnoreCase("PROMOTIONAL"))) {
            throw new BadRequestException("Invalid post type");
        }

        if (postType.equalsIgnoreCase("PROMOTIONAL")) {
            if (request.getCtaText() == null || request.getCtaLink() == null) {
                throw new BadRequestException("CTA text and link are required for promotional posts");
            }
        }

        post.setContent(request.getContent());
        post.setPostType(postType.toUpperCase());
        post.setPinned(request.getPinned() != null && request.getPinned());
        post.setCtaText(request.getCtaText());
        post.setCtaLink(request.getCtaLink());
        post.setScheduledAt(request.getScheduledAt());

        Post updatedPost = postRepository.save(post);

        // ---------- UPDATE HASHTAGS ----------
        postHashtagRepository.deleteByPost(updatedPost);

        List<Hashtag> hashtags = new ArrayList<>();
        if (request.getHashtags() != null) {
            for (String tag : request.getHashtags()) {
                if (tag == null || tag.trim().isEmpty()) continue;

                String normalized = tag.trim().toLowerCase();

                Hashtag hashtag = hashtagRepository
                        .findByTagName(normalized)
                        .orElseGet(() ->
                                hashtagRepository.save(
                                        Hashtag.builder().tagName(normalized).build()
                                )
                        );

                postHashtagRepository.save(
                        PostHashtag.builder()
                                .post(updatedPost)
                                .hashtag(hashtag)
                                .build()
                );

                hashtags.add(hashtag);
            }
        }

        // ---------- UPDATE PRODUCT / SERVICE TAGS ----------
        postTagRepository.deleteByPost(updatedPost);

        // ---------- PRODUCT / SERVICE TAGS ----------
        List<TagResponse> tagResponses = new ArrayList<>();

        if (request.getTags() != null) {
            for (TagRequest tagReq : request.getTags()) {

                // ✅ STRONG validation (THIS IS THE FIX)
                if (tagReq == null ||
                        tagReq.getTagName() == null ||
                        tagReq.getTagName().trim().isEmpty() ||
                        tagReq.getTagType() == null) {
                    continue;
                }

                PostTag postTag = PostTag.builder()
                        .post(updatedPost)              // or updatedPost in updatePost
                        .tagName(tagReq.getTagName().trim())
                        .tagType(tagReq.getTagType())
                        .build();

                postTagRepository.save(postTag);

                tagResponses.add(
                        TagResponse.builder()
                                .tagName(postTag.getTagName())
                                .tagType(postTag.getTagType())
                                .build()
                );
            }
        }

        return postMapper.toPostResponse(updatedPost, hashtags, tagResponses);
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
            throw new UnauthorizedException("You are not allowed to delete this post");
        }

        postTagRepository.deleteByPost(post);
        postHashtagRepository.deleteByPost(post);
        postRepository.delete(post);
    }

    @Override
    public List<PostResponse> getPostsByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Post> visiblePosts =
                postRepository.findVisiblePostsByUser(
                        user,
                        LocalDateTime.now()
                );

        List<PostResponse> responses = new ArrayList<>();

        // pinned posts first
        visiblePosts.stream()
                .filter(Post::getPinned)
                .forEach(post -> responses.add(
                        postMapper.toPostResponse(
                                post,
                                getHashtagsForPost(post),
                                getTagsForPost(post)
                        )
                ));

        // remaining posts
        visiblePosts.stream()
                .filter(post -> !post.getPinned())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .forEach(post -> responses.add(
                        postMapper.toPostResponse(post, List.of(), List.of())
                ));

        return responses;
    }

    // =========================
    // GET POST BY ID
    // =========================
    @Override
    public PostResponse getPostById(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        List<Hashtag> hashtags = postHashtagRepository.findAll()
                .stream()
                .filter(ph -> ph.getPost().getPostId().equals(postId))
                .map(PostHashtag::getHashtag)
                .toList();

        List<TagResponse> tags = postTagRepository.findByPost(post)
                .stream()
                .map(t -> TagResponse.builder()
                        .tagName(t.getTagName())
                        .tagType(t.getTagType())
                        .build())
                .toList();

        return postMapper.toPostResponse(post, hashtags, tags);
    }

    // =========================
    // PIN / UNPIN (UNCHANGED)
    // =========================
    @Override
    @Transactional
    public PostResponse pinPost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("You cannot pin this post");
        }

        if (post.getScheduledAt() != null &&
                post.getScheduledAt().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Cannot pin a scheduled post");
        }

        postRepository.findByUserAndPinnedTrue(post.getUser())
                .ifPresent(existing -> {
                    existing.setPinned(false);
                    postRepository.save(existing);
                });

        post.setPinned(true);
        Post saved = postRepository.save(post);

        return postMapper.toPostResponse(saved, List.of(), List.of());
    }

    @Override
    @Transactional
    public PostResponse unpinPost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserId().equals(userId)) {
            throw new UnauthorizedException("You cannot unpin this post");
        }

        post.setPinned(false);
        Post saved = postRepository.save(post);

        return postMapper.toPostResponse(saved, List.of(), List.of());
    }

    @Override
    public List<PostResponse> getGlobalFeed(Long viewerUserId) {

        List<Post> visiblePosts =
                postRepository.findByScheduledAtIsNullOrScheduledAtLessThanEqual(
                        LocalDateTime.now()
                );

        List<PostResponse> responses = new ArrayList<>();

        visiblePosts.stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .forEach(post -> {
                    List<Hashtag> hashtags = getHashtagsForPost(post);
                    List<TagResponse> tags = getTagsForPost(post);

                    responses.add(
                            postMapper.toPostResponse(post, hashtags, tags)
                    );
                });

        return responses;
    }

    private List<Hashtag> getHashtagsForPost(Post post) {
        return postHashtagRepository.findAll()
                .stream()
                .filter(ph -> ph.getPost().getPostId().equals(post.getPostId()))
                .map(PostHashtag::getHashtag)
                .toList();
    }

    private List<TagResponse> getTagsForPost(Post post) {
        return postTagRepository.findByPost(post)
                .stream()
                .map(t -> TagResponse.builder()
                        .tagName(t.getTagName())
                        .tagType(t.getTagType())
                        .build())
                .toList();
    }

}