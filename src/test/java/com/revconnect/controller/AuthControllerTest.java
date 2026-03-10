package com.revconnect.controller;

import com.revconnect.dto.request.LoginRequest;
import com.revconnect.dto.request.RegisterRequest;
import com.revconnect.entity.User;
import com.revconnect.service.AuthService;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private Model model;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController authController;

    // ========================
    // REGISTER PAGE
    // ========================
    @Test
    void shouldReturnRegisterPage() {

        String view = authController.registerPage(model);

        assertEquals("auth/register", view);
        verify(model).addAttribute(eq("registerRequest"), any(RegisterRequest.class));
    }

    // ========================
    // REGISTER SUCCESS
    // ========================
    @Test
    void shouldRegisterUserSuccessfully() {

        RegisterRequest request = new RegisterRequest();

        String view = authController.register(request, model);

        assertEquals("auth/login", view);
        verify(model).addAttribute(eq("success"), anyString());
    }

    // ========================
    // REGISTER FAILURE
    // ========================
    @Test
    void shouldReturnErrorIfRegisterFails() {

        RegisterRequest request = new RegisterRequest();

        doThrow(new RuntimeException("User exists"))
                .when(authService).register(request);

        String view = authController.register(request, model);

        assertEquals("auth/register", view);
        verify(model).addAttribute("error", "User exists");
    }

    // ========================
    // LOGIN PAGE
    // ========================
    @Test
    void shouldReturnLoginPage() {

        String view = authController.loginPage(model);

        assertEquals("auth/login", view);
        verify(model).addAttribute(eq("loginRequest"), any(LoginRequest.class));
    }

    // ========================
    // LOGIN SUCCESS
    // ========================
    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request = new LoginRequest();

        User user = new User();
        user.setEmail("test@mail.com");

        when(authService.login(request)).thenReturn(user);

        String view = authController.login(request, session, model);

        assertEquals("redirect:/dashboard", view);

        verify(session).setAttribute(eq("loggedUser"), eq(user));
        verify(session).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
    }

    // ========================
    // LOGIN FAILURE
    // ========================
    @Test
    void shouldReturnLoginError() {

        LoginRequest request = new LoginRequest();

        when(authService.login(request))
                .thenThrow(new RuntimeException("Invalid credentials"));

        String view = authController.login(request, session, model);

        assertEquals("auth/login", view);
        verify(model).addAttribute("error", "Invalid credentials");
    }

    // ========================
    // LOGOUT
    // ========================
    @Test
    void shouldLogoutSuccessfully() {

        String view = authController.logout(session, model);

        assertEquals("auth/login", view);
        verify(session).invalidate();
        verify(model).addAttribute("success", "Logout Successful");
    }

    // ========================
    // FORGOT PASSWORD PAGE
    // ========================
    @Test
    void shouldReturnForgotPasswordPage() {

        String view = authController.forgotPasswordPage();

        assertEquals("auth/forgot-password", view);
    }

    // ========================
    // GET SECURITY QUESTION
    // ========================
    @Test
    void shouldReturnSecurityQuestion() {

        User user = new User();
        user.setSecurityQuestion("Your pet?");

        when(authService.findByEmail("test@mail.com")).thenReturn(user);

        String view = authController.getQuestion("test@mail.com", model);

        assertEquals("auth/reset-password", view);

        verify(model).addAttribute("email", "test@mail.com");
        verify(model).addAttribute("question", "Your pet?");
    }

    // ========================
    // RESET PASSWORD SUCCESS
    // ========================
    @Test
    void shouldResetPasswordSuccessfully() {

        String view = authController.resetPassword(
                "test@mail.com",
                "dog",
                "newpass",
                model
        );

        assertEquals("auth/login", view);

        verify(authService).resetPassword("test@mail.com", "dog", "newpass");
        verify(model).addAttribute(eq("success"), anyString());
    }

    // ========================
// RESET PASSWORD FAILURE
// ========================
    @Test
    void shouldReturnResetPageWhenResetFails() {

        User user = new User();
        user.setSecurityQuestion("Your pet?");

        doThrow(new RuntimeException("Wrong answer"))
                .when(authService)
                .resetPassword("test@mail.com", "dog", "newpass");

        when(authService.findByEmail("test@mail.com")).thenReturn(user);

        String view = authController.resetPassword(
                "test@mail.com",
                "dog",
                "newpass",
                model
        );

        assertEquals("auth/reset-password", view);

        verify(model).addAttribute("error", "Wrong answer");
        verify(model).addAttribute("email", "test@mail.com");
        verify(model).addAttribute("question", "Your pet?");
    }
}