package org.nure.atark.autoinsure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nure.atark.autoinsure.controller.PaymentController;
import org.nure.atark.autoinsure.dto.PaymentDto;
import org.nure.atark.autoinsure.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentDto paymentDto;

    @BeforeEach
    void setUp() {
        paymentDto = new PaymentDto();
        paymentDto.setId(1);
       paymentDto.setPaymentDate(OffsetDateTime.now().toLocalDate());
        paymentDto.setPaymentMethod("Credit Card");
        paymentDto.setPolicyId(1001);
    }

    @Test
    void testGetAllPayments() {
        when(paymentService.getAllPayments()).thenReturn(Arrays.asList(paymentDto));

        ResponseEntity<List<PaymentDto>> response = paymentController.getAllPayments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(paymentDto.getId(), response.getBody().get(0).getId());
    }

    @Test
    void testGetPaymentById_Found() {
        when(paymentService.getPaymentById(1)).thenReturn(Optional.of(paymentDto));

        ResponseEntity<?> response = paymentController.getPaymentById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(paymentDto.getId(), ((PaymentDto) response.getBody()).getId());
    }


    @Test
    void testGetPaymentById_NotFound() {
        when(paymentService.getPaymentById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = paymentController.getPaymentById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"error\":\"Payment not found\"}", response.getBody());
    }


    @Test
    void testCreatePayment() {
        when(paymentService.createPayment(paymentDto)).thenReturn(paymentDto);

        ResponseEntity<?> response = paymentController.createPayment(paymentDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(paymentDto.getId(), ((PaymentDto) response.getBody()).getId());
    }

}
