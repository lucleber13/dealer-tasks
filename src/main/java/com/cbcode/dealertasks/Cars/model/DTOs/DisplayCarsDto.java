package com.cbcode.dealertasks.Cars.model.DTOs;

import com.cbcode.dealertasks.Cars.model.Enums.CarStockSold;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.Objects;

public class DisplayCarsDto {
    private Long id;
    private String regNumber;
    private String chassisNumber;
    private String model;
    private String color;
    private Integer keyNumber;
    @Enumerated(value = EnumType.STRING)
    private CarStockSold carStockSold;

    public DisplayCarsDto() {
    }

    public DisplayCarsDto(Long id, String regNumber, String chassisNumber, String model, String color,
                          Integer keyNumber, CarStockSold carStockSold) {
        this.id = id;
        this.regNumber = regNumber;
        this.chassisNumber = chassisNumber;
        this.model = model;
        this.color = color;
        this.keyNumber = keyNumber;
        this.carStockSold = carStockSold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getKeyNumber() {
        return keyNumber;
    }

    public void setKeyNumber(Integer keyNumber) {
        this.keyNumber = keyNumber;
    }

    public CarStockSold getCarStockSold() {
        return carStockSold;
    }

    public void setCarStockSold(CarStockSold carStockSold) {
        this.carStockSold = carStockSold;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DisplayCarsDto that = (DisplayCarsDto) o;
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getRegNumber(), that.getRegNumber())
                && Objects.equals(getChassisNumber(), that.getChassisNumber())
                && Objects.equals(getModel(), that.getModel())
                && Objects.equals(getColor(), that.getColor())
                && Objects.equals(getKeyNumber(), that.getKeyNumber())
                && getCarStockSold() == that.getCarStockSold();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getRegNumber(), getChassisNumber(), getModel(), getColor(), getKeyNumber(), getCarStockSold());
    }
}
