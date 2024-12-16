package org.nure.atark.autoinsure.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "car_types")
public class CarType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_type_id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "car_type_name", nullable = false)
    private String carTypeName;

    @NotNull
    @Column(name = "min_tire_pressure", nullable = false)
    private Double minTirePressure;

    @NotNull
    @Column(name = "max_tire_pressure", nullable = false)
    private Double maxTirePressure;

    @NotNull
    @Column(name = "min_fuel_level", nullable = false)
    private Double minFuelLevel;

    @NotNull
    @Column(name = "max_fuel_level", nullable = false)
    private Double maxFuelLevel;

    @NotNull
    @Column(name = "min_engine_temp", nullable = false)
    private Double minEngineTemp;

    @NotNull
    @Column(name = "max_engine_temp", nullable = false)
    private Double maxEngineTemp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCarTypeName() {
        return carTypeName;
    }

    public void setCarTypeName(String carTypeName) {
        this.carTypeName = carTypeName;
    }

    public Double getMinTirePressure() {
        return minTirePressure;
    }

    public void setMinTirePressure(Double minTirePressure) {
        this.minTirePressure = minTirePressure;
    }

    public Double getMaxTirePressure() {
        return maxTirePressure;
    }

    public void setMaxTirePressure(Double maxTirePressure) {
        this.maxTirePressure = maxTirePressure;
    }

    public Double getMinFuelLevel() {
        return minFuelLevel;
    }

    public void setMinFuelLevel(Double minFuelLevel) {
        this.minFuelLevel = minFuelLevel;
    }

    public Double getMaxFuelLevel() {
        return maxFuelLevel;
    }

    public void setMaxFuelLevel(Double maxFuelLevel) {
        this.maxFuelLevel = maxFuelLevel;
    }

    public Double getMinEngineTemp() {
        return minEngineTemp;
    }

    public void setMinEngineTemp(Double minEngineTemp) {
        this.minEngineTemp = minEngineTemp;
    }

    public Double getMaxEngineTemp() {
        return maxEngineTemp;
    }

    public void setMaxEngineTemp(Double maxEngineTemp) {
        this.maxEngineTemp = maxEngineTemp;
    }

}