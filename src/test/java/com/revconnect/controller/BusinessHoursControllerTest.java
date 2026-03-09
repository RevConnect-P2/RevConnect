package com.revconnect.controller;

import com.revconnect.dto.request.BusinessHoursRequest;
import com.revconnect.dto.response.BusinessHoursResponse;
import com.revconnect.service.ProfileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BusinessHoursControllerTest {

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private BusinessHoursController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    // ================= ADD BUSINESS HOURS =================

    @Test
    void testAddBusinessHours() throws Exception {

        mockMvc.perform(post("/profiles/1/business-hours")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().string("Business hours saved successfully"));

        verify(profileService).addBusinessHours(eq(1L), anyList());
    }

    // ================= GET BUSINESS HOURS =================

    @Test
    void testGetBusinessHours() throws Exception {

        when(profileService.getBusinessHours(1L)).thenReturn(List.of());

        mockMvc.perform(get("/profiles/1/business-hours"))
                .andExpect(status().isOk());

        verify(profileService).getBusinessHours(1L);
    }

    // ================= UPDATE BUSINESS HOURS =================

    @Test
    void testUpdateBusinessHours() throws Exception {

        mockMvc.perform(put("/profiles/1/business-hours/MONDAY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Business hours updated successfully"));

        verify(profileService).updateBusinessHours(eq(1L), eq("MONDAY"), any());
    }

    // ================= DELETE SINGLE DAY =================

    @Test
    void testDeleteBusinessHours() throws Exception {

        mockMvc.perform(delete("/profiles/1/business-hours/MONDAY"))
                .andExpect(status().isOk())
                .andExpect(content().string("Business hours deleted for MONDAY"));

        verify(profileService).deleteBusinessHours(1L, "MONDAY");
    }

    // ================= DELETE ALL =================

    @Test
    void testDeleteAllBusinessHours() throws Exception {

        mockMvc.perform(delete("/profiles/1/business-hours"))
                .andExpect(status().isOk())
                .andExpect(content().string("All business hours deleted successfully"));

        verify(profileService).deleteAllBusinessHours(1L);
    }
}