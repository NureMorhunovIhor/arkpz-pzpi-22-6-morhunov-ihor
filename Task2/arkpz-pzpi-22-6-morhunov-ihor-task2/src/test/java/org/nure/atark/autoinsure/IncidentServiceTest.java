package org.nure.atark.autoinsure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nure.atark.autoinsure.dto.IncidentDto;
import org.nure.atark.autoinsure.entity.Incident;
import org.nure.atark.autoinsure.repository.IncidentRepository;
import org.nure.atark.autoinsure.service.IncidentService;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IncidentServiceTest {

    private IncidentRepository incidentRepository;
    private IncidentService incidentService;

    @BeforeEach
    void setUp() {
        incidentRepository = mock(IncidentRepository.class);
        incidentService = new IncidentService(incidentRepository);
    }

    @Test
    void testGetAllIncidents() {
        Incident incident1 = new Incident();
        incident1.setId(1);
        incident1.setIncidentType("Type1");

        Incident incident2 = new Incident();
        incident2.setId(2);
        incident2.setIncidentType("Type2");

        when(incidentRepository.findAll()).thenReturn(Arrays.asList(incident1, incident2));

        var incidents = incidentService.getAllIncidents();

        assertEquals(2, incidents.size());
        assertEquals("Type1", incidents.get(0).getIncidentType());
        verify(incidentRepository, times(1)).findAll();
    }


    @Test
    void testGetIncidentById_IncidentExists() {
        Incident incident = new Incident();
        incident.setId(1);
        incident.setIncidentType("Type1");

        when(incidentRepository.findById(1)).thenReturn(Optional.of(incident));

        Optional<IncidentDto> foundIncident = incidentService.getIncidentById(1);
        assertTrue(foundIncident.isPresent());
        assertEquals("Type1", foundIncident.get().getIncidentType());
    }

    @Test
    void testGetIncidentById_IncidentDoesNotExist() {
        when(incidentRepository.findById(1)).thenReturn(Optional.empty());

        Optional<IncidentDto> foundIncident = incidentService.getIncidentById(1);
        assertTrue(foundIncident.isEmpty());
    }

    @Test
    void testCreateIncident() {
        IncidentDto incidentDto = new IncidentDto();
        incidentDto.setIncidentType("Type1");
        incidentDto.setDescription("Description");

        Incident savedIncident = new Incident();
        savedIncident.setId(1);
        savedIncident.setIncidentType("Type1");
        savedIncident.setDescription("Description");

        when(incidentRepository.save(Mockito.any(Incident.class))).thenReturn(savedIncident);

        IncidentDto result = incidentService.createIncident(incidentDto);
        assertNotNull(result);
        assertEquals("Type1", result.getIncidentType());
    }

    @Test
    void testUpdateIncident() {
        IncidentDto incidentDto = new IncidentDto();
        incidentDto.setIncidentType("UpdatedType");

        Incident incident = new Incident();
        incident.setId(1);
        incident.setIncidentType("OldType");

        when(incidentRepository.existsById(1)).thenReturn(true);
        incident.setIncidentType("UpdatedType");
        when(incidentRepository.save(Mockito.any(Incident.class))).thenReturn(incident);


        Optional<IncidentDto> updatedIncident = incidentService.updateIncident(1, incidentDto);
        assertTrue(updatedIncident.isPresent());
        assertEquals("UpdatedType", updatedIncident.get().getIncidentType());
    }

    @Test
    void testDeleteIncident() {
        when(incidentRepository.existsById(1)).thenReturn(true);
        doNothing().when(incidentRepository).deleteById(1);

        boolean result = incidentService.deleteIncident(1);
        assertTrue(result);
        verify(incidentRepository, times(1)).deleteById(1);
    }
}
