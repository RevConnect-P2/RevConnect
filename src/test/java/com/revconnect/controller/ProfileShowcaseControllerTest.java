package com.revconnect.controller;

import com.revconnect.service.ProfileShowcaseService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
class ProfileShowcaseControllerTest {

    @Mock
    private ProfileShowcaseService profileShowcaseService;

    @InjectMocks
    private ProfileShowcaseController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ================= ADD SHOWCASE =================

    @Test
    void testAddShowcase() throws Exception {

        mockMvc.perform(post("/profiles/1/showcase")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Showcase added successfully"));

        verify(profileShowcaseService).addShowcase(eq(1L), any());
    }

    // ================= GET SHOWCASES =================

    @Test
    void testGetShowcases() throws Exception {

        when(profileShowcaseService.getShowcases(1L))
                .thenReturn(List.of());

        mockMvc.perform(get("/profiles/1/showcase"))
                .andExpect(status().isOk());

        verify(profileShowcaseService).getShowcases(1L);
    }

    // ================= UPDATE SHOWCASE =================

    @Test
    void testUpdateShowcase() throws Exception {

        mockMvc.perform(put("/profiles/1/showcase/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Showcase updated successfully"));

        verify(profileShowcaseService).updateShowcase(eq(1L), eq(10L), any());
    }

    // ================= DELETE SHOWCASE =================

    @Test
    void testDeleteShowcase() throws Exception {

        mockMvc.perform(delete("/profiles/1/showcase/10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Showcase deleted successfully"));

        verify(profileShowcaseService).deleteShowcase(1L, 10L);
    }
}