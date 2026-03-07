package com.revconnect.dto.response;

import com.revconnect.enums.TagType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponse {

    private String tagName;
    private TagType tagType;
}