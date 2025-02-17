package com.cbcode.dealertasks.Tasks.model.DTOs;

import com.cbcode.dealertasks.Cars.model.DTOs.CarDto;

public class TaskWithCarDto {

    private TaskDto taskDto;
    private CarDto carDto;

    public TaskDto getTaskDto() {
        return taskDto;
    }

    public void setTaskDto(TaskDto taskDto) {
        this.taskDto = taskDto;
    }

    public CarDto getCarDto() {
        return carDto;
    }

    public void setCarDto(CarDto carDto) {
        this.carDto = carDto;
    }
}
