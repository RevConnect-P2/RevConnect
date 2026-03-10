package com.revconnect.security.service;

import com.revconnect.entity.User;
import com.revconnect.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    // ✅ Test when user exists
    @Test
    void shouldLoadUserByUsername() {

        User user = new User();
        user.setEmail("john@test.com");
        user.setPassword("password123");

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("john@test.com");

        assertEquals("john@test.com", details.getUsername());
        assertEquals("password123", details.getPassword());
    }

    // ❌ Test when user not found
    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("missing@test.com")
        );
    }
}