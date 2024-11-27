package org.nure.atark.autoinsure.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Date;

public class PaymentDto {

    private Integer id;
    private LocalDate paymentDate;
    private String paymentMethod;
    private Integer policyId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Integer getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Integer policyId) {
        this.policyId = policyId;
    }
}
