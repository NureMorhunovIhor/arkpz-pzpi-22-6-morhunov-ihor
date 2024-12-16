package org.nure.atark.autoinsure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nure.atark.autoinsure.controller.MaintenanceController;
import org.nure.atark.autoinsure.dto.MaintenanceDto;
import org.nure.atark.autoinsure.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MaintenanceController.class)
@Import(MaintenanceControllerTest.TestConfig.class)
class MaintenanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MaintenanceService maintenanceService;

    @Test
    void testGetAllMaintenance() throws Exception {
        MaintenanceDto maintenanceDto1 = new MaintenanceDto();
        maintenanceDto1.setId(1);
        maintenanceDto1.setMaintenanceType("Type1");

        MaintenanceDto maintenanceDto2 = new MaintenanceDto();
        maintenanceDto2.setId(2);
        maintenanceDto2.setMaintenanceType("Type2");

        when(maintenanceService.getAllMaintenance()).thenReturn(Arrays.asList(maintenanceDto1, maintenanceDto2));

        mockMvc.perform(get("/api/maintenance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(maintenanceService, times(1)).getAllMaintenance();
    }

    @Test
    void testGetMaintenanceById() throws Exception {
        MaintenanceDto maintenanceDto = new MaintenanceDto();
        maintenanceDto.setId(1);
        maintenanceDto.setMaintenanceType("Type1");

        when(maintenanceService.getMaintenanceById(1)).thenReturn(Optional.of(maintenanceDto));

        mockMvc.perform(get("/api/maintenance/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maintenanceType").value("Type1"));

        verify(maintenanceService, times(1)).getMaintenanceById(1);
    }

    @Test
    void testCreateMaintenance() throws Exception {
        MaintenanceDto maintenanceDto = new MaintenanceDto();
        maintenanceDto.setMaintenanceType("Type1");
        maintenanceDto.setDescription("Description");
        maintenanceDto.setCost(BigDecimal.valueOf(100.0));

        MaintenanceDto createdMaintenanceDto = new MaintenanceDto();
        createdMaintenanceDto.setId(1);
        createdMaintenanceDto.setMaintenanceType("Type1");

        when(maintenanceService.createMaintenance(Mockito.any(MaintenanceDto.class))).thenReturn(createdMaintenanceDto);

        mockMvc.perform(post("/api/maintenance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maintenanceDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maintenanceType").value("Type1"));

        verify(maintenanceService, times(1)).createMaintenance(Mockito.any(MaintenanceDto.class));
    }


    @Test
    void testUpdateMaintenance() throws Exception {
        MaintenanceDto maintenanceDto = new MaintenanceDto();
        maintenanceDto.setMaintenanceType("UpdatedType");
        maintenanceDto.setDescription("Updated Description");
        maintenanceDto.setCost(BigDecimal.valueOf(100.0));
        maintenanceDto.setCarId(1);

        MaintenanceDto updatedMaintenanceDto = new MaintenanceDto();
        updatedMaintenanceDto.setId(1);
        updatedMaintenanceDto.setMaintenanceType("UpdatedType");
        updatedMaintenanceDto.setDescription("Updated Description");
        updatedMaintenanceDto.setCost(BigDecimal.valueOf(200.0));

        when(maintenanceService.updateMaintenance(eq(1), any(MaintenanceDto.class))).thenReturn(Optional.of(updatedMaintenanceDto));

        mockMvc.perform(put("/api/maintenance/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(maintenanceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maintenanceType").value("UpdatedType"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.cost").value(200.0));

        verify(maintenanceService, times(1)).updateMaintenance(eq(1), any(MaintenanceDto.class));
    }


    @Test
    void testDeleteMaintenance() throws Exception {
        when(maintenanceService.deleteMaintenance(1)).thenReturn(true);

        mockMvc.perform(delete("/api/maintenance/1"))
                .andExpect(status().isNoContent());

        verify(maintenanceService, times(1)).deleteMaintenance(1);
    }

    static class TestConfig {
        @Bean
        public MaintenanceService maintenanceService() {
            return Mockito.mock(MaintenanceService.class);
        }
    }
}
