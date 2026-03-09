package com.revconnect.dto.request;

import com.revconnect.enums.TagType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagRequestTest {

    @Test
    void testNoArgsConstructorAndSetters() {

        TagRequest request = new TagRequest();

        request.setTagName("Laptop");
        request.setTagType(TagType.PRODUCT);

        assertEquals("Laptop", request.getTagName());
        assertEquals(TagType.PRODUCT, request.getTagType());
    }

    @Test
    void testAllArgsConstructor() {

        TagRequest request =
                new TagRequest("Camera", TagType.SERVICE);

        assertEquals("Camera", request.getTagName());
        assertEquals(TagType.SERVICE, request.getTagType());
    }

    @Test
    void testBuilder() {

        TagRequest request = TagRequest.builder()
                .tagName("Phone")
                .tagType(TagType.PRODUCT)
                .build();

        assertEquals("Phone", request.getTagName());
        assertEquals(TagType.PRODUCT, request.getTagType());
    }
}