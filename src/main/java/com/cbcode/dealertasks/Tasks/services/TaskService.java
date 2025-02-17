package com.cbcode.dealertasks.Tasks.services;

import com.cbcode.dealertasks.Cars.model.DTOs.CarDto;
import com.cbcode.dealertasks.Tasks.model.DTOs.TaskDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskDto createTask(TaskDto taskDto, CarDto carDto);

    TaskDto getTaskById(Long id);

    TaskDto updateTaskStatus(Long id, TaskDto taskDto);

    void deleteTaskById(Long id);

    Page<TaskDto> getAllTasks(Pageable pageable);

    Page<TaskDto> getAllTasksByUserId(Long userId, Pageable pageable);

    Page<TaskDto> getAllTasksByCarId(Long carId, Pageable pageable);

}
