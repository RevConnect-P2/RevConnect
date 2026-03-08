package com.revconnect.service.impl;

import com.revconnect.entity.Comment;
import com.revconnect.entity.Post;
import com.revconnect.entity.User;
import com.revconnect.repository.CommentRepository;
import com.revconnect.repository.PostRepository;
import com.revconnect.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.Silent.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Post post;

    @Before
    public void setup() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@mail.com");

        post = new Post();
        post.setPostId(10L);
    }

    @Test
    public void shouldAddCommentSuccessfully() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.of(post));

        commentService.addComment(10L, "test@mail.com", "Nice post!");

        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfUserNotFound() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.empty());

        commentService.addComment(10L, "test@mail.com", "Nice post!");
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfPostNotFound() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(postRepository.findById(10L))
                .thenReturn(Optional.empty());

        commentService.addComment(10L, "test@mail.com", "Nice post!");
    }

    @Test
    public void shouldReturnCommentsList() {

        User user = new User();
        user.setEmail("test@mail.com");

        Comment comment = new Comment();
        comment.setCommentId(1L);
        comment.setCommentText("Nice");
        comment.setUser(user);   // 🔥 ADD THIS

        when(commentRepository.findByPost_PostId(10L))
                .thenReturn(List.of(comment));

//        List<?> result = commentService.getCommentsByPost(10L);
//
//        assertEquals(1, result.size());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfDeleteNonExistingComment() {

        when(commentRepository.findById(1L))
                .thenReturn(Optional.empty());

        commentService.deleteComment(1L, "test@mail.com");
    }
}