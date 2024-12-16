package org.nure.atark.autoinsure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.nure.atark.autoinsure.dto.MaintenanceDto;
import org.nure.atark.autoinsure.entity.Maintenance;
import org.nure.atark.autoinsure.repository.MaintenanceRepository;
import org.nure.atark.autoinsure.service.MaintenanceService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MaintenanceServiceTest {

    private MaintenanceRepository maintenanceRepository;
    private MaintenanceService maintenanceService;

    @BeforeEach
    void setUp() {
        maintenanceRepository = mock(MaintenanceRepository.class);
        maintenanceService = new MaintenanceService(maintenanceRepository);
    }

    @Test
    void testGetAllMaintenance() {
        var maintenance = new Maintenance();
        maintenance.setId(1);
        maintenance.setMaintenanceType("Oil Change");
        maintenance.setCost(BigDecimal.valueOf(100.0));
        maintenance.setDescription("Change engine oil");

        when(maintenanceRepository.findAll()).thenReturn(Arrays.asList(maintenance));

        var result = maintenanceService.getAllMaintenance();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Oil Change", result.get(0).getMaintenanceType());
        verify(maintenanceRepository, times(1)).findAll();
    }

    @Test
    void testGetMaintenanceById() {
        var maintenance = new Maintenance();
        maintenance.setId(1);
        maintenance.setMaintenanceType("Oil Change");
        maintenance.setCost(BigDecimal.valueOf(100.0));

        when(maintenanceRepository.findById(1)).thenReturn(Optional.of(maintenance));

        var result = maintenanceService.getMaintenanceById(1);

        assertTrue(result.isPresent());
        assertEquals("Oil Change", result.get().getMaintenanceType());
        verify(maintenanceRepository, times(1)).findById(1);
    }

    @Test
    void testCreateMaintenance() {
        var maintenanceDto = new MaintenanceDto();
        maintenanceDto.setMaintenanceType("Oil Change");
        maintenanceDto.setCost(BigDecimal.valueOf(100.0));
        maintenanceDto.setDescription("Change engine oil");

        var maintenance = new Maintenance();
        maintenance.setId(1);
        maintenance.setMaintenanceType("Oil Change");
        maintenance.setCost(BigDecimal.valueOf(100.0));
        maintenance.setDescription("Change engine oil");

        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);

        MaintenanceDto createdMaintenance = maintenanceService.createMaintenance(maintenanceDto);

        assertNotNull(createdMaintenance);
        assertEquals("Oil Change", createdMaintenance.getMaintenanceType());
        verify(maintenanceRepository, times(1)).save(any(Maintenance.class));
    }

    @Test
    void testUpdateMaintenance() {
        var maintenanceDto = new MaintenanceDto();
        maintenanceDto.setMaintenanceType("Tire Change");

        var maintenance = new Maintenance();
        maintenance.setId(1);
        maintenance.setMaintenanceType("Tire Change");

        when(maintenanceRepository.existsById(1)).thenReturn(true);
        when(maintenanceRepository.save(any(Maintenance.class))).thenReturn(maintenance);

        var result = maintenanceService.updateMaintenance(1, maintenanceDto);

        assertTrue(result.isPresent());
        assertEquals("Tire Change", result.get().getMaintenanceType());
        verify(maintenanceRepository, times(1)).existsById(1);
        verify(maintenanceRepository, times(1)).save(any(Maintenance.class));
    }

    @Test
    void testUpdateMaintenance_NotFound() {
        var maintenanceDto = new MaintenanceDto();
        maintenanceDto.setMaintenanceType("Tire Change");

        when(maintenanceRepository.existsById(1)).thenReturn(false);

        var result = maintenanceService.updateMaintenance(1, maintenanceDto);

        assertFalse(result.isPresent());
        verify(maintenanceRepository, times(1)).existsById(1);
        verify(maintenanceRepository, never()).save(any(Maintenance.class));
    }

    @Test
    void testDeleteMaintenance() {
        when(maintenanceRepository.existsById(1)).thenReturn(true);
        doNothing().when(maintenanceRepository).deleteById(1);

        boolean result = maintenanceService.deleteMaintenance(1);

        assertTrue(result);
        verify(maintenanceRepository, times(1)).existsById(1);
        verify(maintenanceRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteMaintenance_NotFound() {
        when(maintenanceRepository.existsById(1)).thenReturn(false);

        boolean result = maintenanceService.deleteMaintenance(1);

        assertFalse(result);
        verify(maintenanceRepository, times(1)).existsById(1);
        verify(maintenanceRepository, never()).deleteById(1);
    }
}
