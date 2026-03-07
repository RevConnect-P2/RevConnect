package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostHashtag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM PostHashtag ph WHERE ph.post = :post")
    void deleteByPost(Post post);
}
