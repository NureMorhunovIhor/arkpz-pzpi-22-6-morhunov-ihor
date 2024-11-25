package org.nure.atark.autoinsure.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @Column(name = "Payment_id", nullable = false)
    private Integer id;

    @ColumnDefault("getdate()")
    @Column(name = "PaymentDate", nullable = false)
    private LocalDate paymentDate;

    @Column(name = "PaymentMethod", nullable = false, length = 50)
    private String paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "Policy_id", nullable = false)
    private org.nure.atark.autoinsure.entity.Policy policy;

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

    public org.nure.atark.autoinsure.entity.Policy getPolicy() {
        return policy;
    }

    public void setPolicy(org.nure.atark.autoinsure.entity.Policy policy) {
        this.policy = policy;
    }

}