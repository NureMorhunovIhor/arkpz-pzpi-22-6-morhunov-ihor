package org.nure.atark.autoinsure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MeasurementDto {

    private Integer id;
    private Integer sensorId;
    private LocalDateTime readingTime;
    private String parameterType;
    private BigDecimal value;

    public MeasurementDto() {
    }

    public MeasurementDto(Integer id, Integer sensorId, LocalDateTime readingTime, String parameterType, BigDecimal value) {
        this.id = id;
        this.sensorId = sensorId;
        this.readingTime = readingTime;
        this.parameterType = parameterType;
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSensorId() {
        return sensorId;
    }

    public void setSensorId(Integer sensorId) {
        this.sensorId = sensorId;
    }

    public LocalDateTime getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
