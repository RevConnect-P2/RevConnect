package com.revconnect.security.jwt;

import com.revconnect.security.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.context.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private UserDetails userDetails;

    @BeforeEach
    void setup() {

        userDetails = User
                .withUsername("test@example.com")
                .password("password")
                .authorities("USER")
                .build();

        SecurityContextHolder.clearContext();
    }

    // 1️⃣ No Authorization header
    @Test
    void shouldContinueFilterWhenHeaderMissing() throws Exception {

        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    // 2️⃣ Header exists but not Bearer
    @Test
    void shouldContinueFilterWhenHeaderNotBearer() throws Exception {

        when(request.getHeader("Authorization")).thenReturn("Basic token");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    // 3️⃣ Valid token authentication
    @Test
    void shouldAuthenticateUserWhenTokenValid() throws Exception {

        String token = "validToken";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtUtil.extractEmail(token))
                .thenReturn("test@example.com");

        when(userDetailsService.loadUserByUsername("test@example.com"))
                .thenReturn(userDetails);

        when(jwtUtil.validateToken(token, "test@example.com"))
                .thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}