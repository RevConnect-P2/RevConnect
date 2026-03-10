package com.revconnect.controller;

import com.revconnect.entity.Connection;
import com.revconnect.entity.User;
import com.revconnect.enums.ConnectionStatus;
import com.revconnect.repository.ConnectionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.Model;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NetworkPageControllerTest {

    @Mock
    private ConnectionRepository connectionRepository;

    @Mock
    private Model model;

    @InjectMocks
    private NetworkPageController networkPageController;

    private MockHttpSession session;

    @BeforeEach
    void setup() {
        session = new MockHttpSession();
    }

    // ================= USER NOT LOGGED IN =================

    @Test
    void networkPage_shouldRedirectToLogin_whenUserNotLoggedIn() {

        String view = networkPageController.networkPage(session, model);

        assertEquals("redirect:/login", view);

        verifyNoInteractions(connectionRepository);
    }

    // ================= USER LOGGED IN =================

    @Test
    void networkPage_shouldLoadNetworkPage_whenUserLoggedIn() {

        User user = new User();
        user.setUserId(1L);

        session.setAttribute("loggedUser", user);

        Connection connection = new Connection();
        Connection pending = new Connection();

        when(connectionRepository.findAllAcceptedConnections(1L))
                .thenReturn(List.of(connection));

        when(connectionRepository.findByReceiver_UserIdAndStatus(
                1L, ConnectionStatus.PENDING))
                .thenReturn(List.of(pending));

        String view = networkPageController.networkPage(session, model);

        assertEquals("connection/network", view);

        verify(model).addAttribute("connections", List.of(connection));
        verify(model).addAttribute("pendingRequests", List.of(pending));
        verify(model).addAttribute("loggedUser", user);

        verify(connectionRepository)
                .findAllAcceptedConnections(1L);

        verify(connectionRepository)
                .findByReceiver_UserIdAndStatus(1L, ConnectionStatus.PENDING);
    }
}