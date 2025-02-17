package com.cbcode.dealertasks.Cars.model;

import com.cbcode.dealertasks.Cars.model.Enums.CarStockSold;
import com.cbcode.dealertasks.Tasks.model.Task;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "cars", uniqueConstraints = {
        @UniqueConstraint(columnNames = "reg_number"),
        @UniqueConstraint(columnNames = "chassis_number")
})
@SequenceGenerator(name = "cars_seq", sequenceName = "cars_seq", allocationSize = 1, initialValue = 1)
public class Car implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cars_seq")
    private Long id;

    @Column(name = "model", nullable = false)
    private String model;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "reg_number", nullable = false, unique = true)
    private String regNumber;

    @Column(name = "chassis_number", nullable = false, unique = true)
    private String chassisNumber;

    @Column(name = "key_number", nullable = false)
    private Integer keyNumber;

    @Column(name = "date_created", updatable = false)
    @CreatedDate
    private LocalDateTime dateCreated = LocalDateTime.now();

    @Column(name = "buyer_name")
    private String buyerName;

    @Column(name = "car_stock_sold")
    @Enumerated(EnumType.STRING)
    private CarStockSold carStockSold;

    @Column(name = "handover_date")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm", shape = JsonFormat.Shape.STRING, timezone = "Europe/London")
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime handoverDate;

    @OneToOne(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private Task task;

    private void validateHandoverDate() {
        if (this.carStockSold == CarStockSold.SOLD && this.handoverDate == null) {
            throw new IllegalArgumentException("Handover date is required for sold cars");
        }
    }

    public Car() {
    }

    public Car(String model, String color, String regNumber, String chassisNumber, Integer keyNumber, String buyerName, LocalDateTime handoverDate) {
        this.model = model;
        this.color = color;
        this.regNumber = regNumber;
        this.chassisNumber = chassisNumber;
        this.keyNumber = keyNumber;
        this.buyerName = buyerName;
        this.handoverDate = handoverDate;
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

    public CarStockSold getCarStockSold() {
        return carStockSold;
    }

    public void setCarStockSold(CarStockSold carStockSold) {
        this.carStockSold = carStockSold;
    }

    public LocalDateTime getHandoverDate() {
        return handoverDate;
    }

    public void setHandoverDate(LocalDateTime handoverDate) {
        this.handoverDate = handoverDate;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(getId(), car.getId())
                && Objects.equals(getModel(), car.getModel())
                && Objects.equals(getColor(), car.getColor())
                && Objects.equals(getRegNumber(), car.getRegNumber())
                && Objects.equals(getChassisNumber(), car.getChassisNumber())
                && Objects.equals(getKeyNumber(), car.getKeyNumber())
                && Objects.equals(getDateCreated(), car.getDateCreated())
                && Objects.equals(getBuyerName(), car.getBuyerName())
                && getCarStockSold() == car.getCarStockSold()
                && Objects.equals(getHandoverDate(), car.getHandoverDate())
                && Objects.equals(getTask(), car.getTask());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getModel(), getColor(), getRegNumber(), getChassisNumber(), getKeyNumber(),
                getDateCreated(), getBuyerName(), getCarStockSold(), getHandoverDate(), getTask());
    }
}
