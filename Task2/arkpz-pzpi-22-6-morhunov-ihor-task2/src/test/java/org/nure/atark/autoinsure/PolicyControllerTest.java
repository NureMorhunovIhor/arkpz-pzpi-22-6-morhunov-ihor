package org.nure.atark.autoinsure;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nure.atark.autoinsure.controller.PolicyController;
import org.nure.atark.autoinsure.dto.PolicyDto;
import org.nure.atark.autoinsure.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PolicyControllerTest {

     @Mock
    private PolicyService policyService;

    @InjectMocks
    private PolicyController policyController;

    @Test
    void testGetAllPolicies() {
        List<PolicyDto> policyList = List.of(new PolicyDto(), new PolicyDto());
        when(policyService.getAllPolicies()).thenReturn(policyList);

        ResponseEntity<List<PolicyDto>> response = policyController.getAllPolicies();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(policyList.size(), response.getBody().size());
    }

    @Test
    void testGetPolicyByIdFound() {
        PolicyDto policyDto = new PolicyDto();
        when(policyService.getPolicyById(1)).thenReturn(Optional.of(policyDto));

        ResponseEntity<?> response = policyController.getPolicyById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetPolicyByIdNotFound() {
        when(policyService.getPolicyById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = policyController.getPolicyById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testCreatePolicy() {
        PolicyDto policyDto = new PolicyDto();
        when(policyService.createPolicy(policyDto)).thenReturn(policyDto);

        ResponseEntity<?> response = policyController.createPolicy(policyDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdatePolicy() {
        PolicyDto policyDto = new PolicyDto();
        when(policyService.updatePolicy(1, policyDto)).thenReturn(Optional.of(policyDto));

        ResponseEntity<?> response = policyController.updatePolicy(1, policyDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdatePolicyNotFound() {
        PolicyDto policyDto = new PolicyDto();
        when(policyService.updatePolicy(1, policyDto)).thenReturn(Optional.empty());

        ResponseEntity<?> response = policyController.updatePolicy(1, policyDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeletePolicy() {
        when(policyService.deletePolicy(1)).thenReturn(true);

        ResponseEntity<?> response = policyController.deletePolicy(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeletePolicyNotFound() {
        when(policyService.deletePolicy(1)).thenReturn(false);

        ResponseEntity<?> response = policyController.deletePolicy(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


}
