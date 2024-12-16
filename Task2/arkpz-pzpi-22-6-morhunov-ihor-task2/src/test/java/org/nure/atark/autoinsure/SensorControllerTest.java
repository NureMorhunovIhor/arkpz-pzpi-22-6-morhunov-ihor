package org.nure.atark.autoinsure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nure.atark.autoinsure.controller.SensorController;
import org.nure.atark.autoinsure.dto.SensorDto;
import org.nure.atark.autoinsure.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SensorController.class)
@Import(SensorControllerTest.TestConfig.class)
class SensorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SensorService sensorService;

    @Test
    void testGetAllSensors() throws Exception {
        SensorDto sensorDto1 = new SensorDto();
        sensorDto1.setId(1);
        sensorDto1.setSensorType("Tire Pressure");
        sensorDto1.setCurrentState("Normal");
        sensorDto1.setLastUpdate(OffsetDateTime.now().toLocalDate());
        sensorDto1.setCarId(1);

        SensorDto sensorDto2 = new SensorDto();
        sensorDto2.setId(2);
        sensorDto2.setSensorType("Fuel Level");
        sensorDto2.setCurrentState("Full");
        sensorDto2.setLastUpdate(OffsetDateTime.now().toLocalDate());
        sensorDto2.setCarId(1);

        when(sensorService.getAllSensors()).thenReturn(Arrays.asList(sensorDto1, sensorDto2));

        mockMvc.perform(get("/api/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[0].sensorType").value("Tire Pressure"))
                .andExpect(jsonPath("$[1].sensorType").value("Fuel Level"));

        verify(sensorService, times(1)).getAllSensors();
    }

    @Test
    void testGetSensorById() throws Exception {
        SensorDto sensorDto = new SensorDto();
        sensorDto.setId(1);
        sensorDto.setSensorType("Tire Pressure");
        sensorDto.setCurrentState("Normal");
        sensorDto.setLastUpdate(OffsetDateTime.now().toLocalDate());
        sensorDto.setCarId(1);

        when(sensorService.getSensorById(1)).thenReturn(Optional.of(sensorDto));

        mockMvc.perform(get("/api/sensors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sensorType").value("Tire Pressure"));

        verify(sensorService, times(1)).getSensorById(1);
    }

    @Test
    void testCreateSensor() throws Exception {
        SensorDto sensorDto = new SensorDto();
        sensorDto.setSensorType("Tire Pressure");
        sensorDto.setCurrentState("Normal");
        sensorDto.setLastUpdate(LocalDate.from(OffsetDateTime.now()));
        sensorDto.setCarId(1);

        SensorDto createdSensor = new SensorDto();
        createdSensor.setId(1);
        createdSensor.setSensorType(sensorDto.getSensorType());
        createdSensor.setCurrentState(sensorDto.getCurrentState());
        createdSensor.setLastUpdate(sensorDto.getLastUpdate());
        createdSensor.setCarId(sensorDto.getCarId());

        when(sensorService.createSensor(Mockito.any(SensorDto.class))).thenReturn(createdSensor);

        mockMvc.perform(post("/api/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensorDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sensorType").value("Tire Pressure"));

        verify(sensorService, times(1)).createSensor(Mockito.any(SensorDto.class));
    }

    @Test
    void testUpdateSensor() throws Exception {
        SensorDto sensorDto = new SensorDto();
        sensorDto.setSensorType("Tire Pressure");
        sensorDto.setCurrentState("Low");
        sensorDto.setLastUpdate(OffsetDateTime.now().toLocalDate());
        sensorDto.setCarId(1);

        SensorDto updatedSensor = new SensorDto();
        updatedSensor.setId(1);
        updatedSensor.setSensorType(sensorDto.getSensorType());
        updatedSensor.setCurrentState(sensorDto.getCurrentState());
        updatedSensor.setLastUpdate(sensorDto.getLastUpdate());
        updatedSensor.setCarId(sensorDto.getCarId());

        when(sensorService.updateSensor(eq(1), any(SensorDto.class))).thenReturn(Optional.of(updatedSensor));

        mockMvc.perform(put("/api/sensors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensorDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorType").value("Tire Pressure"))
                .andExpect(jsonPath("$.currentState").value("Low"));

        verify(sensorService, times(1)).updateSensor(eq(1), any(SensorDto.class));
    }

    @Test
    void testDeleteSensor() throws Exception {
        when(sensorService.deleteSensor(1)).thenReturn(true);

        mockMvc.perform(delete("/api/sensors/1"))
                .andExpect(status().isNoContent());

        verify(sensorService, times(1)).deleteSensor(1);
    }

    static class TestConfig {
        @Bean
        public SensorService sensorService() {
            return Mockito.mock(SensorService.class);
        }
    }
}
