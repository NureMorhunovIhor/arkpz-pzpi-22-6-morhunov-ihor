package org.nure.atark.autoinsure.dto;

import com.paypal.api.payments.Payment;

import java.util.List;
import java.util.stream.Collectors;

public class PaymentResponse {
    private String id;
    private String state;
    private String intent;
    private List<TransactionResponse> transactions;

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.state = payment.getState();
        this.intent = payment.getIntent();

        // Преобразуем транзакции
        if (payment.getTransactions() != null) {
            this.transactions = payment.getTransactions().stream()
                    .map(TransactionResponse::new)
                    .collect(Collectors.toList());
        }
    }

    // Геттеры
    public String getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public String getIntent() {
        return intent;
    }

    public List<TransactionResponse> getTransactions() {
        return transactions;
    }

    // Сеттеры
    public void setId(String id) {
        this.id = id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public void setTransactions(List<TransactionResponse> transactions) {
        this.transactions = transactions;
    }
}
