package com.cbcode.dealertasks.Tasks.services.impl;

import com.cbcode.dealertasks.Cars.model.Car;
import com.cbcode.dealertasks.Cars.model.DTOs.CarDto;
import com.cbcode.dealertasks.Cars.model.Enums.CarStockSold;
import com.cbcode.dealertasks.Cars.repository.CarRepository;
import com.cbcode.dealertasks.ExceptionsConfig.ArgumentNotPresentException;
import com.cbcode.dealertasks.ExceptionsConfig.CarAlreadyExistsException;
import com.cbcode.dealertasks.ExceptionsConfig.UserNotFoundException;
import com.cbcode.dealertasks.Tasks.model.DTOs.TaskDto;
import com.cbcode.dealertasks.Tasks.model.Task;
import com.cbcode.dealertasks.Tasks.repository.TaskRepository;
import com.cbcode.dealertasks.Tasks.services.TaskService;
import com.cbcode.dealertasks.Users.model.User;
import com.cbcode.dealertasks.Users.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CarRepository carRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository, ModelMapper modelMapper, CarRepository carRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.carRepository = carRepository;
    }

    /**
     * This method creates a task for a user. It first gets the authentication object.
     * If the authentication object is null or not authenticated, it throws an AccessDeniedException.
     * It then gets the user details from the authentication object. If the user is not found, it throws a UserNotFoundException.
     * It then creates the task for the user. It then saves the task in the database.
     * It saved the car in the database.
     * @param taskDto - TaskDto object to be created in the database.
     * @param carDto - The carDto object associated with the task.
     * @return - The created TaskDto object.
     */
    @Override
    @Transactional
    public TaskDto createTask(TaskDto taskDto, CarDto carDto) {
        logger.info("Attempting to create task for user with id: {}", taskDto.getCreatedBy());
        Authentication authentication = getAuthentication();
        User user = getUser(authentication);

        if (taskDto == null) {
            logger.error("Task cannot be null");
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (carDto == null) {
            logger.error("Car cannot be null");
            throw new IllegalArgumentException("Car cannot be null");
        }

        logger.debug("Authenticated user: {}", user.getEmail());
        if (!authentication.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority ->
                        grantedAuthority.getAuthority().equals("ROLE_SALES"))) {
            logger.error("User '{}' does not have the required role to create a task", user.getEmail());
            throw new AccessDeniedException("User does not have the required role to create a task");
        }

        logger.debug("Validating car constraints for car with registration number: {} or by chassis number: {}", carDto.getRegNumber(), carDto.getChassisNumber());
        validateCarConstraints(carDto);

        Car car = modelMapper.map(carDto, Car.class);
        if (car == null) {
            logger.error("Failed to map CarDto to Car Entity");
            throw new IllegalArgumentException("Car entity cannot be null");
        }
        logger.debug("Mapped CarDto to Car Entity: {}", car);

        Task task = modelMapper.map(taskDto, Task.class);
        if (task == null) {
            logger.error("Failed to map TaskDto to Task Entity");
            throw new IllegalArgumentException("Task entity cannot be null");
        }
        task.setCreatedBy(userRepository.findByEmail(user.getEmail()).orElseThrow(() -> {
            logger.error("User not found with email: {}", user.getEmail());
            return new UserNotFoundException("User not found");
        }));

        task.setCar(car);
        logger.debug("Mapped TaskDto to Task Entity: {}", task);

        logger.info("Saving task and car in the database");
        task = taskRepository.save(task);
        logger.info("Task and car saved successfully with task id: {}", task.getId());

        TaskDto createdTaskDto = modelMapper.map(task, TaskDto.class);
        logger.debug("Mapped Task Entity to TaskDto: {}", createdTaskDto);
        return createdTaskDto;
    }

    /**
     * This method validates the car constraints.
     * If the registration number or chassis number is null, it throws an IllegalArgumentException.
     * @param carDto - The carDto object to be validated.
     */
    private void validateCarConstraints(CarDto carDto) {
        logger.debug("Validating car constraints");

        if (carRepository.existsByRegNumber(carDto.getRegNumber())) {
            logger.error("Car with registration number: {} already exists", carDto.getRegNumber());
            throw new CarAlreadyExistsException("Car already exists");
        }

        if (carRepository.existsByChassisNumber(carDto.getChassisNumber())) {
            logger.error("Car with chassis number: {} already exists", carDto.getChassisNumber());
            throw new CarAlreadyExistsException("Car already exists");
        }

        if (carDto.getRegNumber() == null || carDto.getRegNumber().isEmpty()) {
            logger.error("Car registration number is required");
            throw new IllegalArgumentException("Car registration number is required");
        }
        if (carDto.getChassisNumber() == null || carDto.getChassisNumber().isEmpty()) {
            logger.error("Car chassis number is required");
            throw new IllegalArgumentException("Car chassis number is required");
        }
        if (carDto.getCarStockSold().equals(CarStockSold.SOLD)) {
            if (carDto.getBuyerName() == null || carDto.getBuyerName().isEmpty()) {
                logger.error("Buyer name is required for a sold car");
                throw new ArgumentNotPresentException("Buyer name is required when car is sold");
            }
            if (carDto.getHandoverDate() == null || carDto.getHandoverDate().toString().isEmpty()) {
                logger.error("Handover date is required for a sold car");
                throw new ArgumentNotPresentException("Handover date is required when car is sold");
            }
        }
    }

    /**
     * This method gets the authentication object.
     * If the authentication object is null or not authenticated, it throws an AccessDeniedException.
     * @return - The authentication object.
     * @throws AccessDeniedException - If the user is not authenticated.
     */
    private Authentication getAuthentication() {
        logger.info("Getting authentication object");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User not authenticated");
            throw new AccessDeniedException("User not authenticated");
        }
        return authentication;
    }

    /**
     * This method gets the user details from the authentication object.
     * If the user is not found, it throws a UserNotFoundException.
     * @param authentication - The authentication object.
     * @return - The user object fetched.
     * @throws UserNotFoundException - If the user is not found.
     */
    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName()).orElseThrow(() -> {
            logger.error("User not found with email: {}", authentication.getName());
            return new UserNotFoundException("User not found");
        });
    }

    /**
     * @param id
     * @return
     */
    @Override
    public TaskDto getTaskById(Long id) {
        return null;
    }

    /**
     * @param id
     * @param taskDto
     * @return
     */
    @Override
    public TaskDto updateTaskStatus(Long id, TaskDto taskDto) {
        return null;
    }

    /**
     * @param id
     */
    @Override
    public void deleteTaskById(Long id) {

    }

    /**
     * @param pageable
     * @return
     */
    @Override
    public Page<TaskDto> getAllTasks(Pageable pageable) {
        return null;
    }

    /**
     * @param userId
     * @param pageable
     * @return
     */
    @Override
    public Page<TaskDto> getAllTasksByUserId(Long userId, Pageable pageable) {
        return null;
    }

    /**
     * @param carId
     * @param pageable
     * @return
     */
    @Override
    public Page<TaskDto> getAllTasksByCarId(Long carId, Pageable pageable) {
        return null;
    }
}
