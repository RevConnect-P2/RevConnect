package com.revconnect.dto.response;

import com.revconnect.enums.TagType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagResponseTest {

    @Test
    void testNoArgsConstructorAndSetters() {

        TagResponse response = new TagResponse();

        response.setTagName("Laptop");
        response.setTagType(TagType.PRODUCT);

        assertEquals("Laptop", response.getTagName());
        assertEquals(TagType.PRODUCT, response.getTagType());
    }

    @Test
    void testAllArgsConstructor() {

        TagResponse response =
                new TagResponse("Camera", TagType.SERVICE);

        assertEquals("Camera", response.getTagName());
        assertEquals(TagType.SERVICE, response.getTagType());
    }

    @Test
    void testBuilder() {

        TagResponse response = TagResponse.builder()
                .tagName("Phone")
                .tagType(TagType.PRODUCT)
                .build();

        assertEquals("Phone", response.getTagName());
        assertEquals(TagType.PRODUCT, response.getTagType());
    }
}