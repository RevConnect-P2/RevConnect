package com.revconnect.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShowcaseResponse {
    private Long showcaseId;
    private String title;
    private String description;
    private Double price;
    private String imageUrl;
}