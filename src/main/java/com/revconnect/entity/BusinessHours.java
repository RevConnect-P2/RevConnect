package com.revconnect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BUSINESS_HOURS")

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

    @ManyToOne
    @JoinColumn(name = "PROFILE_ID")
    private UserProfile profile;

    @Column(name = "DAY_OF_WEEK")
    private String dayOfWeek;

    @Column(name = "OPEN_TIME")
    private LocalDateTime openTime;

    @Column(name = "CLOSE_TIME")
    private LocalDateTime closeTime;

    @Column(name = "IS_CLOSED")
    private Boolean isClosed;

}