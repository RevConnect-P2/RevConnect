package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "BUSINESS_HOURS",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"PROFILE_ID", "DAY_OF_WEEK"})
        }
)

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BusinessHours {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HOUR_ID")
    private Long hourId;


    // Each business profile has multiple business hours
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PROFILE_ID", nullable = false)
    private UserProfile profile;


    // MONDAY, TUESDAY, etc
    @Column(name = "DAY_OF_WEEK", nullable = false, length = 20)
    private String dayOfWeek;


    @Column(name = "OPEN_TIME")
    private LocalDateTime openTime;


    @Column(name = "CLOSE_TIME")
    private LocalDateTime closeTime;


    // Oracle stores Boolean as NUMBER(1)
    @Column(name = "IS_CLOSED")
    private Boolean isClosed = false;



    // Validate before insert/update
    @PrePersist
    @PreUpdate
    private void validateHours() {

        if (Boolean.FALSE.equals(isClosed)) {

            if (openTime == null || closeTime == null) {

                throw new IllegalStateException(
                        "Open and Close time must be provided if business is open"
                );
            }

            if (closeTime.isBefore(openTime)) {

                throw new IllegalStateException(
                        "Close time cannot be before open time"
                );
            }

        }

    }

}