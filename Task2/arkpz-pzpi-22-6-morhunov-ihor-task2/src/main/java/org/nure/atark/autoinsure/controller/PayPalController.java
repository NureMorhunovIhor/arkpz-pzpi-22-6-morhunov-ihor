package org.nure.atark.autoinsure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.Payment;

import com.paypal.base.rest.PayPalRESTException;
import org.nure.atark.autoinsure.dto.PaymentDto;
import org.nure.atark.autoinsure.dto.PaymentResponse;
import org.nure.atark.autoinsure.dto.PolicyDto;
import org.nure.atark.autoinsure.entity.Policy;
import org.nure.atark.autoinsure.service.PayPalService;
import org.nure.atark.autoinsure.service.PaymentService;
import org.nure.atark.autoinsure.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PayPalController {

    @Autowired
    private PayPalService payPalService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    PolicyService policyService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestParam Double total, @RequestParam String currency,
                                 @RequestParam String description, @RequestParam Integer policyId) {
        try {
            String cancelUrl = "http://localhost:8081/api/payments/cancel";
            String successUrl = "http://localhost:8081/api/payments/success?policyId=" + policyId;
            Payment payment = payPalService.createPayment(total, currency, "paypal", "sale", description, cancelUrl, successUrl);

            String approvalUrl = payPalService.getApprovalUrl(payment);
            if (approvalUrl != null) {
                return ResponseEntity.ok(approvalUrl);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"Approval URL not found\"}");
            }
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }


    @GetMapping("/success")
    public ResponseEntity<?> successPayment(@RequestParam String paymentId, @RequestParam String PayerID,
                                            @RequestParam Integer policyId) {
        try {
            Payment payment = payPalService.executePayment(paymentId, PayerID);

            PaymentResponse response = new PaymentResponse(payment);

            PaymentDto paymentDto = new PaymentDto();
            paymentDto.setPaymentDate(LocalDate.now());
            paymentDto.setPaymentMethod(payment.getPayer().getPaymentMethod());
            paymentDto.setPolicyId(policyId);


            paymentService.createPayment(paymentDto);

            return ResponseEntity.ok(response);
        } catch (PayPalRESTException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }





    @GetMapping("/cancel")
    public ResponseEntity<?> cancelPayment() {
        return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"Payment canceled\"}");
    }
}
