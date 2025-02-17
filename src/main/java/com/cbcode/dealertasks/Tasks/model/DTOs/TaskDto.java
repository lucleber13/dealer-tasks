package com.cbcode.dealertasks.Tasks.model.DTOs;

import com.cbcode.dealertasks.Cars.model.DTOs.CarDto;
import com.cbcode.dealertasks.Tasks.model.Enums.TaskPriority;
import com.cbcode.dealertasks.Tasks.model.Enums.TaskStatus;
import com.cbcode.dealertasks.Users.model.DTOs.UserDto;
import com.cbcode.dealertasks.Valet.model.DTOs.ValetDto;
import com.cbcode.dealertasks.Workshop.model.DTOs.WorkshopDto;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

public class TaskDto {
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus = TaskStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private TaskPriority taskPriority = TaskPriority.MEDIUM;

    private Timestamp deadline;

    private UserDto createdBy;

    private CarDto car;

    private WorkshopDto workshop;

    private ValetDto valet;

    public TaskDto() {
    }

    public TaskDto(Long id, String title, String description, TaskStatus taskStatus, TaskPriority taskPriority, Timestamp deadline, UserDto createdBy,
                   CarDto car, WorkshopDto workshop, ValetDto valet) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskPriority = taskPriority;
        this.deadline = deadline;
        this.createdBy = createdBy;
        this.car = car;
        this.workshop = workshop;
        this.valet = valet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public Timestamp getDeadline() {
        return deadline;
    }

    public void setDeadline(Timestamp deadline) {
        this.deadline = deadline;
    }

    public UserDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDto createdBy) {
        this.createdBy = createdBy;
    }

    public CarDto getCar() {
        return car;
    }

    public void setCar(CarDto car) {
        this.car = car;
    }

    public WorkshopDto getWorkshop() {
        return workshop;
    }

    public void setWorkshop(WorkshopDto workshop) {
        this.workshop = workshop;
    }

    public ValetDto getValet() {
        return valet;
    }

    public void setValet(ValetDto valet) {
        this.valet = valet;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TaskDto taskDto = (TaskDto) o;
        return Objects.equals(getId(), taskDto.getId())
                && Objects.equals(getTitle(), taskDto.getTitle())
                && Objects.equals(getDescription(), taskDto.getDescription())
                && getTaskStatus() == taskDto.getTaskStatus()
                && getTaskPriority() == taskDto.getTaskPriority()
                && Objects.equals(getDeadline(), taskDto.getDeadline())
                && Objects.equals(getCreatedBy(), taskDto.getCreatedBy())
                && Objects.equals(getCar(), taskDto.getCar())
                && Objects.equals(getWorkshop(), taskDto.getWorkshop())
                && Objects.equals(getValet(), taskDto.getValet());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getTitle(), getDescription(), getTaskStatus(), getTaskPriority(),
                getDeadline(), getCreatedBy(), getCar(), getWorkshop(), getValet());
    }
}
