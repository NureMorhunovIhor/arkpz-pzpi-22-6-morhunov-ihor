package org.nure.atark.autoinsure.dto;

import org.nure.atark.autoinsure.entity.CarType;

public class CarDto {

    private Integer id;
    private String licensePlate;
    private String brand;
    private String model;
    private Integer year;
    private Integer userId;
    private Integer carTypeId;

    public CarDto(Integer id, String licensePlate, String brand, String model, Integer year, Integer userId, Integer carTypeId) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.userId = userId;
        this.carTypeId = carTypeId;
    }

    public CarDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCarTypeId() {
        return carTypeId;
    }

    public void setCarTypeId(Integer carType) {
        this.carTypeId = carType;
    }
}
