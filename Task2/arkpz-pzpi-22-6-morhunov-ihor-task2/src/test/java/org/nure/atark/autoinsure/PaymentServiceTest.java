package org.nure.atark.autoinsure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nure.atark.autoinsure.dto.PaymentDto;
import org.nure.atark.autoinsure.entity.Payment;
import org.nure.atark.autoinsure.entity.Policy;
import org.nure.atark.autoinsure.repository.PaymentRepository;
import org.nure.atark.autoinsure.service.PaymentService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentDto paymentDto;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentDto = new PaymentDto();
        paymentDto.setId(1);
        paymentDto.setPaymentDate(LocalDate.now());
        paymentDto.setPaymentMethod("Credit Card");
        paymentDto.setPolicyId(1001);

        payment = new Payment();
        payment.setId(1);
        paymentDto.setPaymentDate(OffsetDateTime.now().toLocalDate());
        payment.setPaymentMethod("Credit Card");

        Policy policy = new Policy();
        policy.setId(1001);
        payment.setPolicy(policy);
    }

    @Test
    void testGetAllPayments() {
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));

        List<PaymentDto> result = paymentService.getAllPayments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentDto.getId(), result.get(0).getId());
    }

    @Test
    void testGetPaymentById_Found() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(payment));

        Optional<PaymentDto> result = paymentService.getPaymentById(1);

        assertTrue(result.isPresent());
        assertEquals(paymentDto.getId(), result.get().getId());
    }

    @Test
    void testGetPaymentById_NotFound() {
        when(paymentRepository.findById(1)).thenReturn(Optional.empty());

        Optional<PaymentDto> result = paymentService.getPaymentById(1);

        assertFalse(result.isPresent());
    }

    @Test
    void testCreatePayment() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentDto result = paymentService.createPayment(paymentDto);

        assertNotNull(result);
        assertEquals(paymentDto.getId(), result.getId());
        assertEquals(paymentDto.getPaymentMethod(), result.getPaymentMethod());
    }
}
