package org.nure.atark.autoinsure.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Entity
@Table(name = "sensors")
public class Sensor {
    @Id
    @Column(name = "Sensor_id", nullable = false)
    private Integer id;

    @Column(name = "SensorType", nullable = false, length = 50)
    private String sensorType;

    @Column(name = "CurrentState", length = 50)
    private String currentState;

    @ColumnDefault("getdate()")
    @Column(name = "LastUpdate", nullable = false)
    private LocalDate lastUpdate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Car_id", nullable = false)
    private Car car;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSensorType() {
        return sensorType;
    }

    public void setSensorType(String sensorType) {
        this.sensorType = sensorType;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

}