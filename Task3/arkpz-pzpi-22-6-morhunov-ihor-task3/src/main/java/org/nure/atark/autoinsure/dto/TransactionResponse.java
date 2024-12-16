package org.nure.atark.autoinsure.dto;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Transaction;

public class TransactionResponse {
    private String description;
    private Amount amount;

    public TransactionResponse(Transaction transaction) {
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount(); // копируем только необходимые данные
    }

    // Геттеры
    public String getDescription() {
        return description;
    }

    public Amount getAmount() {
        return amount;
    }

    // Сеттеры
    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }
}
