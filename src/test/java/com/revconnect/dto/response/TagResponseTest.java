package com.revconnect.dto.response;

import com.revconnect.enums.TagType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagResponseTest {

    @Test
    void shouldTestGettersAndSetters() {

        TagResponse tag = new TagResponse();

        tag.setTagName("Laptop");
        tag.setTagType(TagType.PRODUCT);

        assertEquals("Laptop", tag.getTagName());
        assertEquals(TagType.PRODUCT, tag.getTagType());
    }

    @Test
    void shouldTestAllArgsConstructor() {

        TagResponse tag = new TagResponse("ServiceTag", TagType.SERVICE);

        assertEquals("ServiceTag", tag.getTagName());
        assertEquals(TagType.SERVICE, tag.getTagType());
    }

    @Test
    void shouldTestBuilder() {

        TagResponse tag = TagResponse.builder()
                .tagName("Phone")
                .tagType(TagType.PRODUCT)
                .build();

        assertEquals("Phone", tag.getTagName());
        assertEquals(TagType.PRODUCT, tag.getTagType());
    }
}