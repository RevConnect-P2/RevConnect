package com.revconnect.entity;

import com.revconnect.enums.TagType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "tag_name", nullable = false)
    private String tagName;

    @Enumerated(EnumType.STRING)
    @Column(name = "tag_type", nullable = false)
    private TagType tagType;
}