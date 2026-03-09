package com.revconnect.service.impl;

import com.revconnect.dto.request.LoginRequest;
import com.revconnect.dto.request.RegisterRequest;
import com.revconnect.entity.User;
import com.revconnect.entity.UserProfile;
import com.revconnect.enums.ProfileType;
import com.revconnect.repository.UserRepository;
import com.revconnect.repository.UserProfileRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;

import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @Before
    public void setup() {

        user = new User();
        user.setUserId(1L);
        user.setEmail("test@mail.com");
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setUserType("PERSONAL");
        user.setSecurityAnswer("blue");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@mail.com");
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("123456");
        registerRequest.setUserType("PERSONAL");
        registerRequest.setSecurityQuestion("color?");
        registerRequest.setSecurityAnswer("blue");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@mail.com");
        loginRequest.setPassword("123456");
    }

    // ---------------- REGISTER ----------------

    @Test
    public void shouldRegisterUserSuccessfully() {

        when(userRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername(registerRequest.getUsername()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User result = authService.register(registerRequest);

        assertNotNull(result);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfEmailAlreadyExists() {

        when(userRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.of(user));

        authService.register(registerRequest);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfUsernameExists() {

        when(userRepository.findByEmail(registerRequest.getEmail()))
                .thenReturn(Optional.empty());

        when(userRepository.findByUsername(registerRequest.getUsername()))
                .thenReturn(Optional.of(user));

        authService.register(registerRequest);
    }

    // ---------------- LOGIN ----------------

    @Test
    public void shouldLoginSuccessfully() {

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encodedPassword"))
                .thenReturn(true);

        User result = authService.login(loginRequest);

        assertNotNull(result);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfEmailNotRegistered() {

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        authService.login(loginRequest);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfPasswordIncorrect() {

        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("123456", "encodedPassword"))
                .thenReturn(false);

        authService.login(loginRequest);
    }

    // ---------------- FIND USER ----------------

    @Test
    public void shouldFindUserByEmail() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        User result = authService.findByEmail("test@mail.com");

        assertEquals("testuser", result.getUsername());
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfUserNotFound() {

        when(userRepository.findByEmail("unknown@mail.com"))
                .thenReturn(Optional.empty());

        authService.findByEmail("unknown@mail.com");
    }

    // ---------------- RESET PASSWORD ----------------

    @Test
    public void shouldResetPasswordSuccessfully() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newpass"))
                .thenReturn("encodedNewPass");

        authService.resetPassword("test@mail.com","blue","newpass");

        verify(userRepository).save(user);
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfSecurityAnswerWrong() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        authService.resetPassword("test@mail.com","wrong","newpass");
    }
}