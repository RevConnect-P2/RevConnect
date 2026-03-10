package com.revconnect.entity;

import com.revconnect.enums.TagType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PostTagTest {

    @Test
    void shouldTestGettersAndSetters() {

        PostTag postTag = new PostTag();
        Post post = new Post();

        postTag.setId(1L);
        postTag.setPost(post);
        postTag.setTagName("Laptop");
        postTag.setTagType(TagType.PRODUCT);

        assertEquals(1L, postTag.getId());
        assertEquals(post, postTag.getPost());
        assertEquals("Laptop", postTag.getTagName());
        assertEquals(TagType.PRODUCT, postTag.getTagType());
    }

    @Test
    void shouldTestBuilder() {

        Post post = new Post();

        PostTag postTag = PostTag.builder()
                .id(2L)
                .post(post)
                .tagName("Consulting")
                .tagType(TagType.SERVICE)
                .build();

        assertEquals(2L, postTag.getId());
        assertEquals(post, postTag.getPost());
        assertEquals("Consulting", postTag.getTagName());
        assertEquals(TagType.SERVICE, postTag.getTagType());
    }

    @Test
    void shouldTestAllArgsConstructor() {

        Post post = new Post();

        PostTag postTag = new PostTag(
                3L,
                post,
                "Camera",
                TagType.PRODUCT
        );

        assertEquals(3L, postTag.getId());
        assertEquals(post, postTag.getPost());
        assertEquals("Camera", postTag.getTagName());
        assertEquals(TagType.PRODUCT, postTag.getTagType());
    }
}