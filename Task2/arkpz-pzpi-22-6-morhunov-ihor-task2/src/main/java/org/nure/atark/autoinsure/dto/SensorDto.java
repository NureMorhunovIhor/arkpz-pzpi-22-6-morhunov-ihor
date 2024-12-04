package org.nure.atark.autoinsure.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class SensorDto {

    private Integer id;
    private String sensorType;
    private String currentState;
    private LocalDate lastUpdate;
    private Integer carId;

    public SensorDto() {
    }

    public SensorDto(Integer id, String sensorType, String currentState, LocalDate lastUpdate, Integer carId) {
        this.id = id;
        this.sensorType = sensorType;
        this.currentState = currentState;
        this.lastUpdate = lastUpdate;
        this.carId = carId;
    }

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

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }
}

