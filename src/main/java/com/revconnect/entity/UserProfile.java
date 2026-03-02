package com.revconnect.entity;

import com.revconnect.enums.ProfileType;
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


    @Column(name = "BIO", columnDefinition = "CLOB")
    private String bio;


    @Column(name = "PROFILE_PIC")
    private String profilePic;


    @Column(name = "LOCATION")
    private String location;


    @Column(name = "WEBSITE")
    private String website;



    /**
     * PERSONAL / CREATOR / BUSINESS
     * Default = PERSONAL
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "PROFILE_TYPE", nullable = false)
    private ProfileType profileType;



    // CREATOR fields

    @Column(name = "CATEGORY")
    private String category;


    @Column(name = "EXTERNAL_LINKS")
    private String externalLinks;



    // BUSINESS fields

    @Column(name = "BUSINESS_ADDRESS")
    private String businessAddress;


    @Column(name = "CONTACT_INFO")
    private String contactInfo;



    // PROFILE SETTINGS

    @Column(name = "PROFILE_VISIBILITY")
    private String profileVisibility;



    // TIMESTAMP

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;


    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;



    /**
     * AUTO CREATE TIMESTAMP + DEFAULT PROFILE TYPE
     */
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();

        if (profileType == null) {

            profileType = ProfileType.PERSONAL;

        }

    }



    /**
     * AUTO UPDATE TIMESTAMP
     */
    @PreUpdate
    protected void onUpdate() {

        updatedAt = LocalDateTime.now();

    }

}