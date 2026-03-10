package com.revconnect.service.impl;

import com.revconnect.dto.request.PostCreateRequest;
import com.revconnect.dto.response.PostResponse;
import com.revconnect.entity.Hashtag;
import com.revconnect.entity.Post;
import com.revconnect.entity.Share;
import com.revconnect.entity.User;
import com.revconnect.exception.BadRequestException;
import com.revconnect.exception.ResourceNotFoundException;
import com.revconnect.mapper.PostMapper;
import com.revconnect.repository.*;
import com.revconnect.service.NotificationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HashtagRepository hashtagRepository;
    @Mock
    private PostHashtagRepository postHashtagRepository;
    @Mock
    private PostTagRepository postTagRepository;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostLikeRepository postLikeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ShareRepository shareRepository;
    @Mock
    private NotificationService notificationService;
    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;
    private PostCreateRequest request;

    @Before
    public void setup() {

        user = new User();
        user.setUserId(1L);

        post = new Post();
        post.setPostId(10L);
        post.setUser(user);

        request = new PostCreateRequest();
        request.setContent("Test Post");
        request.setPostType("NORMAL");
    }

    @Test
    public void shouldCreatePostSuccessfully() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        PostResponse response = postService.createPost(1L, request);

        assertNotNull(response);
        verify(postRepository).save(any(Post.class));
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowIfContentEmpty() {

        request.setContent("");

        postService.createPost(1L, request);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfUserNotFound() {

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        postService.createPost(1L, request);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowInvalidPostType() {

        request.setPostType("INVALID");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        postService.createPost(1L, request);
    }

    @Test
    public void shouldGetPostById() {

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        PostResponse response = postService.getPostById(10L);

        assertNotNull(response);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfPostNotFound() {

        when(postRepository.findById(10L)).thenReturn(Optional.empty());

        postService.getPostById(10L);
    }

    @Test
    public void shouldDeletePostSuccessfully() {

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        postService.deletePost(10L, 1L);

        verify(postRepository).delete(post);
    }

    @Test
    public void shouldReturnPostCount() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.countByUser(user)).thenReturn(5L);

        long count = postService.countPostsByUser(1L);

        assertEquals(5L, count);
    }

    @Test
    public void shouldReturnTrendingHashtags() {

        when(postRepository.findTrendingHashtags(any()))
                .thenReturn(List.of("#java", "#spring"));

        List<String> hashtags = postService.getTrendingHashtags();

        assertEquals(2, hashtags.size());
    }

    @Test
    public void shouldReturnPostsByHashtag() {

        when(postRepository.findPostsByHashtag("#java"))
                .thenReturn(List.of(post));

        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        List<PostResponse> responses =
                postService.getPostsByHashtag("#java");

        assertNotNull(responses);
    }

    @Test(expected = BadRequestException.class)
    public void shouldThrowIfPromotionalPostMissingCTA() {

        request.setPostType("PROMOTIONAL");
        request.setCtaText(null);
        request.setCtaLink(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        postService.createPost(1L, request);
    }

    @Test(expected = com.revconnect.exception.UnauthorizedException.class)
    public void shouldThrowUnauthorizedOnUpdate() {

        User otherUser = new User();
        otherUser.setUserId(2L);

        post.setUser(otherUser);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        postService.updatePost(10L, 1L, request);
    }

    @Test
    public void shouldPinPostSuccessfully() {

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.findByUserAndPinnedTrue(user)).thenReturn(Optional.empty());
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        PostResponse response = postService.pinPost(10L, 1L);

        assertNotNull(response);
        verify(postRepository).save(post);
    }

    @Test
    public void shouldUnpinPostSuccessfully() {

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        PostResponse response = postService.unpinPost(10L, 1L);

        assertNotNull(response);
        verify(postRepository).save(post);
    }

    @Test
    public void shouldReturnGlobalFeed() {

        when(postRepository.findGlobalFeedPosts(anyLong(), any()))
                .thenReturn(List.of(post));

        when(shareRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(Collections.emptyList());

        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        List<PostResponse> responses = postService.getGlobalFeed(1L);

        assertNotNull(responses);
    }

    @Test
    public void shouldReplaceExistingPinnedPost() {

        Post existingPinned = new Post();
        existingPinned.setPinned(true);
        existingPinned.setUser(user);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.findByUserAndPinnedTrue(user))
                .thenReturn(Optional.of(existingPinned));

        when(postRepository.save(any(Post.class))).thenReturn(post);

        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        PostResponse response = postService.pinPost(10L, 1L);

        assertNotNull(response);

        verify(postRepository).save(existingPinned);
    }

    @Test
    public void shouldCreatePostWithHashtags() {

        request.setHashtags(List.of("java", "spring"));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        when(hashtagRepository.findByTagName(any()))
                .thenReturn(Optional.empty());

        when(hashtagRepository.save(any()))
                .thenReturn(new Hashtag());

        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        PostResponse response = postService.createPost(1L, request);

        assertNotNull(response);
    }

    @Test
    public void shouldUpdatePostSuccessfully() {

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        PostResponse response =
                postService.updatePost(10L, 1L, request);

        assertNotNull(response);

        verify(postRepository).save(post);
    }

    @Test(expected = com.revconnect.exception.UnauthorizedException.class)
    public void shouldThrowUnauthorizedOnDelete() {

        User other = new User();
        other.setUserId(2L);

        post.setUser(other);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        postService.deletePost(10L, 1L);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowIfPinPostNotFound() {

        when(postRepository.findById(10L)).thenReturn(Optional.empty());

        postService.pinPost(10L, 1L);
    }

    @Test(expected = com.revconnect.exception.UnauthorizedException.class)
    public void shouldThrowIfPinUnauthorized() {

        User other = new User();
        other.setUserId(2L);

        post.setUser(other);

        when(postRepository.findById(10L)).thenReturn(Optional.of(post));

        postService.pinPost(10L, 1L);
    }

    @Test
    public void shouldReturnPostsByUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(postRepository.findVisiblePostsByUser(any(), any()))
                .thenReturn(List.of(post));

        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        List<PostResponse> responses =
                postService.getPostsByUser(1L);

        assertNotNull(responses);
    }

    @Test
    public void shouldReturnGlobalFeedWithShares() {

        Share share = new Share();
        share.setOriginalPost(post);
        share.setSharedBy(user);

        when(postRepository.findGlobalFeedPosts(anyLong(), any()))
                .thenReturn(List.of(post));

        when(shareRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(share));

        when(postMapper.toPostResponse(any(), any(), any()))
                .thenReturn(new PostResponse());

        List<PostResponse> responses = postService.getGlobalFeed(1L);

        assertNotNull(responses);
    }



}