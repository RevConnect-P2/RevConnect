package com.revconnect.service.impl;

import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @Before
    public void setup() {

        user = new User();
        user.setUserId(1L);
        user.setUsername("john");
        user.setEmail("john@mail.com");
    }

    // ---------------------------
    // getUserIdByUsername tests
    // ---------------------------

    @Test
    public void shouldReturnUserIdWhenUsernameExists() {

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        Long userId = userService.getUserIdByUsername("john");

        assertEquals(Long.valueOf(1), userId);
    }

    @Test
    public void shouldReturnUserIdWhenEmailExists() {

        when(userRepository.findByUsername("john@mail.com"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("john@mail.com"))
                .thenReturn(Optional.of(user));

        Long userId = userService.getUserIdByUsername("john@mail.com");

        assertEquals(Long.valueOf(1), userId);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("unknown"))
                .thenReturn(Optional.empty());

        userService.getUserIdByUsername("unknown");
    }

    // ---------------------------
    // searchUsernames tests
    // ---------------------------

    @Test
    public void shouldReturnMatchingUsernames() {

        User u1 = new User();
        u1.setUsername("john");

        User u2 = new User();
        u2.setUsername("johnny");

        when(userRepository.findByUsernameContainingIgnoreCase("jo"))
                .thenReturn(List.of(u1, u2));

        List<String> result = userService.searchUsernames("jo");

        assertEquals(2, result.size());
        assertEquals("john", result.get(0));
        assertEquals("johnny", result.get(1));
    }
}