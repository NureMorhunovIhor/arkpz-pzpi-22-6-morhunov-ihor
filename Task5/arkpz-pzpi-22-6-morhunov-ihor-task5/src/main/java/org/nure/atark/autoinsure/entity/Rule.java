package org.nure.atark.autoinsure.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Entity
@Table(name = "rules")
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "car_type", length = 50)
    private String carType;

    @Lob
    @Column(name = "formula")
    private String formula;

    @Column(name = "technical_factor_threshold")
    private Integer technicalFactorThreshold;

    @Column(name = "technical_factor_multiplier", precision = 18, scale = 2)
    private BigDecimal technicalFactorMultiplier;

    @Column(name = "base_price", precision = 18, scale = 2) // Додаємо нове поле
    private BigDecimal basePrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Integer getTechnicalFactorThreshold() {
        return technicalFactorThreshold;
    }

    public void setTechnicalFactorThreshold(Integer technicalFactorThreshold) {
        this.technicalFactorThreshold = technicalFactorThreshold;
    }

    public BigDecimal getTechnicalFactorMultiplier() {
        return technicalFactorMultiplier;
    }

    public void setTechnicalFactorMultiplier(BigDecimal technicalFactorMultiplier) {
        this.technicalFactorMultiplier = technicalFactorMultiplier;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
}