package com.revconnect.controller;

import com.revconnect.entity.User;
import com.revconnect.enums.ConnectionStatus;
import com.revconnect.service.ConnectionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ConnectionControllerTest {

    @Mock
    private ConnectionService connectionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {

        ConnectionController controller =
                new ConnectionController(connectionService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // ================= SEND REQUEST =================

    @Test
    void shouldSendConnectionRequest() throws Exception {

        mockMvc.perform(
                post("/connections/request/1/2")
        )
        .andExpect(status().isOk())
        .andExpect(content().string("Request Sent"));

        verify(connectionService)
                .sendConnectionRequest(1L, 2L);
    }

    // ================= ACCEPT REQUEST =================

    @Test
    void shouldAcceptConnection() throws Exception {

        mockMvc.perform(
                post("/connections/accept/10")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/network"));

        verify(connectionService)
                .acceptRequest(10L);
    }

    // ================= REJECT REQUEST =================

    @Test
    void shouldRejectConnection() throws Exception {

        mockMvc.perform(
                post("/connections/reject/10")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/network"));

        verify(connectionService)
                .rejectRequest(10L);
    }

    // ================= REMOVE CONNECTION =================

    @Test
    void shouldRemoveConnection() throws Exception {

        mockMvc.perform(
                post("/connections/remove/10")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/network"));

        verify(connectionService)
                .removeConnection(10L);
    }

    // ================= CONNECTION COUNT =================

    @Test
    void shouldReturnConnectionsCount() throws Exception {

        when(connectionService.getConnectionsCount(1L))
                .thenReturn(5L);

        mockMvc.perform(
                        get("/connections/count/1")
                )
                .andExpect(status().isOk());

        verify(connectionService)
                .getConnectionsCount(1L);
    }

    // ================= CONNECTION STATUS =================

    @Test
    void shouldReturnConnectionStatusAccepted() throws Exception {

        when(connectionService.getConnectionStatus(1L,2L))
                .thenReturn(ConnectionStatus.ACCEPTED);

        mockMvc.perform(
                        get("/connections/status/1/2")
                )
                .andExpect(status().isOk());

        verify(connectionService)
                .getConnectionStatus(1L,2L);
    }

    @Test
    void shouldReturnConnectionStatusNone() throws Exception {

        when(connectionService.getConnectionStatus(1L,2L))
                .thenReturn(null);

        mockMvc.perform(
                        get("/connections/status/1/2")
                )
                .andExpect(status().isOk());

        verify(connectionService)
                .getConnectionStatus(1L,2L);
    }

    // ================= GET MY CONNECTIONS =================

    @Test
    void shouldReturnMyConnections() throws Exception {

        User user = new User();
        user.setUserId(2L);

        when(connectionService.getMyConnections(1L))
                .thenReturn(List.of(user));

        mockMvc.perform(
                get("/connections/my/1")
        )
        .andExpect(status().isOk());
    }
}