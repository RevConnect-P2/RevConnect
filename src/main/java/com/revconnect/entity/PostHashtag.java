package com.revconnect.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "POST_HASHTAGS")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PostHashtag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;


    @ManyToOne
    @JoinColumn(name = "POST_ID", referencedColumnName = "POST_ID")
    private Post post;


    @ManyToOne
    @JoinColumn(name = "HASHTAG_ID", referencedColumnName = "HASHTAG_ID")
    private Hashtag hashtag;


    @Column(name = "TAGGED_AT")
    private LocalDateTime taggedAt;

}