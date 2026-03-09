package com.revconnect.mapper;

import com.revconnect.dto.response.PostResponse;
import com.revconnect.dto.response.TagResponse;
import com.revconnect.entity.Hashtag;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PostMapperTest {

    private PostMapper postMapper;

    @Before
    public void setup() {
        postMapper = new PostMapper();
    }

    // -------- Test with user + hashtags + tags --------

    @Test
    public void shouldMapPostWithUserAndHashtags() {

        User user = new User();
        user.setUserId(1L);
        user.setUsername("john");

        Post post = new Post();
        post.setPostId(10L);
        post.setContent("Hello world");
        post.setUser(user);

        Hashtag tag = new Hashtag();
        tag.setTagName("spring");

        TagResponse tagResponse = new TagResponse();

        PostResponse response = postMapper.toPostResponse(
                post,
                List.of(tag),
                List.of(tagResponse)
        );

        assertEquals(Long.valueOf(10L), response.getPostId());
        assertEquals("Hello world", response.getContent());
        assertEquals(Long.valueOf(1L), response.getUserId());
        assertEquals("john", response.getUsername());

        assertEquals(1, response.getHashtags().size());
        assertEquals("spring", response.getHashtags().get(0));

        assertEquals(1, response.getTags().size());
    }

    // -------- Test null user --------

    @Test
    public void shouldHandleNullUser() {

        Post post = new Post();
        post.setPostId(20L);

        PostResponse response = postMapper.toPostResponse(
                post,
                List.of(),
                List.of()
        );

        assertNull(response.getUserId());
        assertNull(response.getUsername());
    }

    // -------- Test null hashtags --------

    @Test
    public void shouldHandleNullHashtags() {

        Post post = new Post();
        post.setPostId(30L);

        PostResponse response = postMapper.toPostResponse(
                post,
                null,
                List.of()
        );

        assertNotNull(response.getHashtags());
        assertEquals(0, response.getHashtags().size());
    }

    // -------- Test null tags --------

    @Test
    public void shouldHandleNullTags() {

        Post post = new Post();
        post.setPostId(40L);

        PostResponse response = postMapper.toPostResponse(
                post,
                List.of(),
                null
        );

        assertNotNull(response.getTags());
        assertEquals(0, response.getTags().size());
    }
}