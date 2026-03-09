package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {

    // Delete hashtags when post is updated/deleted
    @Modifying
    @Transactional
    @Query("DELETE FROM PostHashtag ph WHERE ph.post = :post")
    void deleteByPost(Post post);

    // Get hashtags for a specific post
    List<PostHashtag> findByPost(Post post);
}