package org.nure.atark.autoinsure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nure.atark.autoinsure.dto.PolicyDto;
import org.nure.atark.autoinsure.entity.Policy;
import org.nure.atark.autoinsure.entity.Car;
import org.nure.atark.autoinsure.repository.PolicyRepository;
import org.nure.atark.autoinsure.service.PolicyService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    // Тест на создание полиса
    @Test
    void testCreatePolicy() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setStartDate(LocalDate.parse("2024-11-01"));  // Используем LocalDate
        policyDto.setEndDate(LocalDate.parse("2025-11-01"));    // Используем LocalDate
        policyDto.setStatus("ACTIVE");
        policyDto.setPrice(BigDecimal.valueOf(500.0));           // Используем BigDecimal
        policyDto.setCarId(1);

        Policy policy = new Policy();
        policy.setStartDate(policyDto.getStartDate());
        policy.setEndDate(policyDto.getEndDate());
        policy.setStatus(policyDto.getStatus());
        policy.setPrice(policyDto.getPrice());

        Car car = new Car();
        car.setId(policyDto.getCarId());
        policy.setCar(car);

        when(policyRepository.save(any(Policy.class))).thenReturn(policy);

        PolicyDto createdPolicy = policyService.createPolicy(policyDto);

        assertNotNull(createdPolicy);
        assertEquals(policyDto.getStartDate(), createdPolicy.getStartDate());
        assertEquals(policyDto.getPrice(), createdPolicy.getPrice());
        verify(policyRepository, times(1)).save(any(Policy.class));
    }

    // Тест на обновление полиса
    @Test
    void testUpdatePolicy() {
        PolicyDto policyDto = new PolicyDto();
        policyDto.setStartDate(LocalDate.parse("2024-12-01"));
        policyDto.setEndDate(LocalDate.parse("2025-12-01"));
        policyDto.setStatus("ACTIVE");
        policyDto.setPrice(BigDecimal.valueOf(600.0));
        policyDto.setCarId(1);

        Policy existingPolicy = new Policy();
        existingPolicy.setId(1);
        existingPolicy.setStartDate(LocalDate.parse("2024-11-01"));
        existingPolicy.setEndDate(LocalDate.parse("2025-11-01"));
        existingPolicy.setStatus("ACTIVE");
        existingPolicy.setPrice(BigDecimal.valueOf(500.0));

        Car car = new Car();
        car.setId(1);
        existingPolicy.setCar(car);

        when(policyRepository.existsById(1)).thenReturn(true);
        when(policyRepository.findById(1)).thenReturn(Optional.of(existingPolicy));
        when(policyRepository.save(any(Policy.class))).thenReturn(existingPolicy);

        Optional<PolicyDto> updatedPolicy = policyService.updatePolicy(1, policyDto);

        assertTrue(updatedPolicy.isPresent());
        assertEquals(policyDto.getStartDate(), updatedPolicy.get().getStartDate());
        assertEquals(policyDto.getPrice(), updatedPolicy.get().getPrice());
    }


    @Test
    void testDeletePolicy() {
        int policyId = 1;
        when(policyRepository.existsById(policyId)).thenReturn(true);

        boolean result = policyService.deletePolicy(policyId);

        assertTrue(result);
        verify(policyRepository, times(1)).deleteById(policyId);
    }

    @Test
    void testDeletePolicyNotFound() {
        int policyId = 1;
        when(policyRepository.existsById(policyId)).thenReturn(false);

        boolean result = policyService.deletePolicy(policyId);

        assertFalse(result);
    }

    // Тест на получение всех полисов
    @Test
    void testGetAllPolicies() {
        List<Policy> policies = List.of(new Policy(), new Policy());
        when(policyRepository.findAll()).thenReturn(policies);

        List<PolicyDto> result = policyService.getAllPolicies();

        assertNotNull(result);
        assertEquals(policies.size(), result.size());
    }

    // Тест на получение полиса по ID
    @Test
    void testGetPolicyById() {
        Policy policy = new Policy();
        policy.setId(1);
        when(policyRepository.findById(1)).thenReturn(Optional.of(policy));

        Optional<PolicyDto> result = policyService.getPolicyById(1);

        assertTrue(result.isPresent());
        assertEquals(policy.getId(), result.get().getId());
    }

    @Test
    void testGetPolicyByIdNotFound() {
        when(policyRepository.findById(1)).thenReturn(Optional.empty());

        Optional<PolicyDto> result = policyService.getPolicyById(1);

        assertFalse(result.isPresent());
    }
}
