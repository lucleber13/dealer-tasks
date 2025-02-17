package com.cbcode.dealertasks.Cars.model.DTOs;

import com.cbcode.dealertasks.Cars.model.Enums.CarStockSold;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Objects;

public class CarDto {

    private Long id;
    private String model;
    private String color;
    private String regNumber;
    private String chassisNumber;
    private Integer keyNumber;
    @CreatedDate
    private LocalDateTime dateCreated;
    private String buyerName;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime handoverDate;
    @Enumerated(EnumType.STRING)
    private CarStockSold carStockSold;

    public CarDto() {
    }

    public CarDto(Long id, String model, String color, String regNumber, String chassisNumber, Integer keyNumber,
                  LocalDateTime dateCreated, String buyerName, LocalDateTime handoverDate, CarStockSold carStockSold) {
        this.id = id;
        this.model = model;
        this.color = color;
        this.regNumber = regNumber;
        this.chassisNumber = chassisNumber;
        this.keyNumber = keyNumber;
        this.dateCreated = dateCreated;
        this.buyerName = buyerName;
        this.handoverDate = handoverDate;
        this.carStockSold = carStockSold;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getKeyNumber() {
        return keyNumber;
    }

    public void setKeyNumber(Integer keyNumber) {
        this.keyNumber = keyNumber;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public LocalDateTime getHandoverDate() {
        return handoverDate;
    }

    public void setHandoverDate(LocalDateTime handoverDate) {
        this.handoverDate = handoverDate;
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
        CarDto carDto = (CarDto) o;
        return Objects.equals(getId(), carDto.getId())
                && Objects.equals(getModel(), carDto.getModel())
                && Objects.equals(getColor(), carDto.getColor())
                && Objects.equals(getRegNumber(), carDto.getRegNumber())
                && Objects.equals(getChassisNumber(), carDto.getChassisNumber())
                && Objects.equals(getKeyNumber(), carDto.getKeyNumber())
                && Objects.equals(getDateCreated(), carDto.getDateCreated())
                && Objects.equals(getBuyerName(), carDto.getBuyerName())
                && Objects.equals(getHandoverDate(), carDto.getHandoverDate())
                && getCarStockSold() == carDto.getCarStockSold();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getModel(), getColor(), getRegNumber(), getChassisNumber(), getKeyNumber(),
                getDateCreated(), getBuyerName(), getHandoverDate(), getCarStockSold());
    }
}
