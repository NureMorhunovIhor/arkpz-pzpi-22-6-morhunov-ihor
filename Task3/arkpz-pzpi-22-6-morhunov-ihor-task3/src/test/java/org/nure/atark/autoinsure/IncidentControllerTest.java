package org.nure.atark.autoinsure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nure.atark.autoinsure.controller.IncidentController;
import org.nure.atark.autoinsure.dto.IncidentDto;
import org.nure.atark.autoinsure.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IncidentController.class)
@Import(IncidentControllerTest.TestConfig.class)
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IncidentService incidentService;

    @Test
    void testGetAllIncidents() throws Exception {
        IncidentDto incidentDto1 = new IncidentDto();
        incidentDto1.setId(1);
        incidentDto1.setIncidentType("Type1");

        IncidentDto incidentDto2 = new IncidentDto();
        incidentDto2.setId(2);
        incidentDto2.setIncidentType("Type2");

        when(incidentService.getAllIncidents()).thenReturn(Arrays.asList(incidentDto1, incidentDto2));

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(incidentService, times(1)).getAllIncidents();
    }

    @Test
    void testGetIncidentById() throws Exception {
        IncidentDto incidentDto = new IncidentDto();
        incidentDto.setId(1);
        incidentDto.setIncidentType("Type1");

        when(incidentService.getIncidentById(1)).thenReturn(Optional.of(incidentDto));

        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidentType").value("Type1"));

        verify(incidentService, times(1)).getIncidentById(1);
    }

    @Test
    void testCreateIncident() throws Exception {
        IncidentDto incidentDto = new IncidentDto();
        incidentDto.setIncidentType("Type1");
        incidentDto.setDescription("Description");

        IncidentDto createdIncidentDto = new IncidentDto();
        createdIncidentDto.setId(1);
        createdIncidentDto.setIncidentType("Type1");

        when(incidentService.createIncident(Mockito.any(IncidentDto.class))).thenReturn(createdIncidentDto);

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incidentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.incidentType").value("Type1"));

        verify(incidentService, times(1)).createIncident(Mockito.any(IncidentDto.class));
    }

    @Test
    void testDeleteIncident() throws Exception {
        when(incidentService.deleteIncident(1)).thenReturn(true);

        mockMvc.perform(delete("/api/incidents/1"))
                .andExpect(status().isNoContent());

        verify(incidentService, times(1)).deleteIncident(1);
    }

    static class TestConfig {
        @Bean
        public IncidentService incidentService() {
            return Mockito.mock(IncidentService.class);
        }
    }
}
