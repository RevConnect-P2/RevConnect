package com.revconnect.repository;

import com.revconnect.entity.Post;
import com.revconnect.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    void deleteByPost(Post post);

    List<PostTag> findByPost(Post post);
}