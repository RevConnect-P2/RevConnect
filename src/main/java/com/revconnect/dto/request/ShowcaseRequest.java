package com.revconnect.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShowcaseRequest {

    private String title;
    private String description;
    private Double price;
    private String imageUrl;
}