package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SHARES")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SHARE_ID")
    private Long shareId;


    @ManyToOne
    @JoinColumn(name = "ORIGINAL_POST_ID")
    private Post originalPost;


    @ManyToOne
    @JoinColumn(name = "SHARED_BY")
    private User sharedBy;


    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

}