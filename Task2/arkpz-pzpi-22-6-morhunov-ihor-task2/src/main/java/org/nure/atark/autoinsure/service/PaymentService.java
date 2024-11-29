package org.nure.atark.autoinsure.service;

import org.nure.atark.autoinsure.dto.PaymentDto;
import org.nure.atark.autoinsure.entity.Payment;
import org.nure.atark.autoinsure.entity.Policy;
import org.nure.atark.autoinsure.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentDto> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PaymentDto> getPaymentById(Integer id) {
        return paymentRepository.findById(id).map(this::convertToDto);
    }

    public PaymentDto createPayment(PaymentDto paymentDto) {
        Payment payment = new Payment();
        payment.setPaymentDate(paymentDto.getPaymentDate());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());

        Policy policy = new Policy();
        policy.setId(paymentDto.getPolicyId());
        payment.setPolicy(policy);

        Payment savedPayment = paymentRepository.save(payment);
        return convertToDto(savedPayment);
    }

    public List<PaymentDto> getPaymentsByUserId(Integer userId) {
        List<Payment> payments = paymentRepository.findPaymentsByUserId(userId);
        return payments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PaymentDto convertToDto(Payment payment) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(payment.getId());
        paymentDto.setPaymentDate(payment.getPaymentDate());
        paymentDto.setPaymentMethod(payment.getPaymentMethod());
        paymentDto.setPolicyId(payment.getPolicy().getId());
        return paymentDto;
    }
}
