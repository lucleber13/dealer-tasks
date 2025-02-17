package com.cbcode.dealertasks.Tasks.controller;

import com.cbcode.dealertasks.Cars.model.DTOs.CarDto;
import com.cbcode.dealertasks.Tasks.model.DTOs.TaskDto;
import com.cbcode.dealertasks.Tasks.model.DTOs.TaskWithCarDto;
import com.cbcode.dealertasks.Tasks.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('ROLE_SALES', 'ROLE_ADMIN')")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TaskDto> createTask(@RequestBody TaskWithCarDto taskWithCarDto) {
        TaskDto taskDto = taskWithCarDto.getTaskDto();
        CarDto carDto = taskWithCarDto.getCarDto();
        return ResponseEntity.ok(taskService.createTask(taskDto, carDto));
    }
}
