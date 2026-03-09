package com.revconnect.controller;

import com.revconnect.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Test
    void testSearchUsers() throws Exception {

        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

        List<String> usernames = List.of("john", "john_doe", "johnny");

        when(userService.searchUsernames(anyString())).thenReturn(usernames);

        mockMvc.perform(get("/users/search")
                .param("keyword", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0]").value("john"))
                .andExpect(jsonPath("$[1]").value("john_doe"))
                .andExpect(jsonPath("$[2]").value("johnny"));

        verify(userService).searchUsernames("john");
    }
}