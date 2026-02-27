package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    void deleteByPost(Post post);
}
