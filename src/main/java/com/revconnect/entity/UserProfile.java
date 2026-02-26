package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_PROFILE")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PROFILE_ID")
    private Long profileId;


    // Each user has only one profile
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;


    @Column(name = "FULL_NAME")
    private String fullName;


    @Column(name = "BIO")
    private String bio;


    @Column(name = "PROFILE_PIC")
    private String profilePic;


    @Column(name = "LOCATION")
    private String location;


    @Column(name = "WEBSITE")
    private String website;


    @Column(name = "CATEGORY")
    private String category;


    @Column(name = "CONTACT_INFO")
    private String contactInfo;


    @Column(name = "BUSINESS_ADDRESS")
    private String businessAddress;


    @Column(name = "EXTERNAL_LINKS")
    private String externalLinks;


    @Column(name = "PROFILE_VISIBILITY")
    private String profileVisibility;


    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    // Oracle timestamp auto insert
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

    }


    // Oracle timestamp auto update
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

    }

}