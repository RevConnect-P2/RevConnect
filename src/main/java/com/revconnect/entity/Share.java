package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "SHARES",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ORIGINAL_POST_ID", "SHARED_BY"})
        }
)

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


    // Post being shared
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORIGINAL_POST_ID", nullable = false)
    private Post originalPost;


    // User who shared
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SHARED_BY", nullable = false)
    private User sharedBy;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    // Auto set created time
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

    }


    // Auto set updated time
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

    }

}