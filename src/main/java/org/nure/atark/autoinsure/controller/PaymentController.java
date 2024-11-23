package org.nure.atark.autoinsure.controller;

import org.nure.atark.autoinsure.dto.PaymentDto;
import org.nure.atark.autoinsure.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Retrieve all payment records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of payments retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<PaymentDto>> getAllPayments() {
        List<PaymentDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Retrieve payment by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment record retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Payment record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Integer id) {
        Optional<PaymentDto> payment = paymentService.getPaymentById(id);
        return payment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new payment record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Payment record created successfully")
    })
    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentDto paymentDto) {
        PaymentDto createdPayment = paymentService.createPayment(paymentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @Operation(summary = "Update an existing payment record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment record updated successfully"),
            @ApiResponse(responseCode = "404", description = "Payment record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PaymentDto> updatePayment(@PathVariable Integer id, @RequestBody PaymentDto paymentDto) {
        Optional<PaymentDto> updatedPayment = paymentService.updatePayment(id, paymentDto);
        return updatedPayment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete payment record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Payment record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Integer id) {
        return paymentService.deletePayment(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
