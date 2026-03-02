package com.revconnect.dto.request;

import com.revconnect.enums.TagType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagRequest {

    private String tagName;
    private TagType tagType;
}