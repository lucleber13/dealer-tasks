package com.cbcode.dealertasks.Cars.service.impl;

import com.cbcode.dealertasks.Cars.model.Car;
import com.cbcode.dealertasks.Cars.model.DTOs.CarDto;
import com.cbcode.dealertasks.Cars.model.DTOs.DisplayCarsDto;
import com.cbcode.dealertasks.Cars.model.Enums.CarStockSold;
import com.cbcode.dealertasks.Cars.repository.CarRepository;
import com.cbcode.dealertasks.Cars.service.CarService;
import com.cbcode.dealertasks.ExceptionsConfig.CarAlreadyExistsException;
import com.cbcode.dealertasks.ExceptionsConfig.CarNotFoundException;
import com.cbcode.dealertasks.ExceptionsConfig.UserNotFoundException;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);
    private final CarRepository carRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    public CarServiceImpl(CarRepository carRepository, ModelMapper modelMapper, UserRepository userRepository) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
    }

    /**
     * This method creates a new car. It receives a carDto object as a parameter.
     * To create a new car, it first gets the authentication object to get the user details.
     * It checks if the car already exists in the database by checking the chassis number and registration number.
     * @param carDto - The carDto object to be created.
     * @return - The carDto object created.
     * @throws CarAlreadyExistsException - If the car already exists in the database.
     * @throws UserNotFoundException - If the user is not found in the database.
     * @throws AccessDeniedException - If the user is not authenticated.
     * @see CarDto - The object that represents the car.
     */
    @Override
    @Transactional
    public CarDto createCar(CarDto carDto) {
        logger.info("Creating a new car with chassis number: {} and registration number: {}", carDto.getChassisNumber(), carDto.getRegNumber());

        var authentication = getAuthentication();
        var user = getUser(authentication);
        //checkSalesPermission(user);
        var car = modelMapper.map(carDto, Car.class);

        if (carRepository.existsByChassisNumber(car.getChassisNumber())) {
            logger.error("Car with chassis number already exists: {}", car.getChassisNumber());
            throw new CarAlreadyExistsException("Car with chassis number already exists");
        }
        if (carRepository.existsByRegNumber(car.getRegNumber())) {
            logger.error("Car with registration number already exists: {}", car.getRegNumber());
            throw new CarAlreadyExistsException("Car with registration number already exists");
        }


        Car savedCar = carRepository.save(car);
        logger.info("Car created successfully with id: {}", savedCar.getId());

        return modelMapper.map(savedCar, CarDto.class);
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
     * @param id - The id of the car to be fetched.
     * @return - The carDto object fetched. If the car is not found, it throws a CarNotFoundException.
     * @throws CarNotFoundException - If the car is not found.
     */
    @Override
    public CarDto getCarById(Long id) {
        logger.info("Getting car by id: {}", id);

        var car = getCar(id);
        logger.info("Car fetched successfully with id: {}", id);

        return modelMapper.map(car, CarDto.class);
    }

    /**
     * This method gets the car object by id.
     * If the car is not found, it throws a CarNotFoundException.
     * @param id - The id of the car to be fetched.
     * @return - The car object fetched.
     * @throws CarNotFoundException - If the car is not found.
     */
    private Car getCar(Long id) {
        return carRepository.findById(id).orElseThrow(() -> {
            logger.error("Car not found with id: {}", id);
            return new CarNotFoundException("Car not found with id: " + id);
        });
    }

    /**
     * @param id - The id of the car to be updated.
     * @param carDto - The carDto object to be updated.
     * @return - The carDto object updated. If the car is not found, it throws a CarNotFoundException.
     */
    @Override
    @Transactional
    public CarDto updateCarToSold(Long id, CarDto carDto) {
        logger.info("Updating car to sold with id: {}", id);

        if (carDto == null || carDto.getCarStockSold() == null) {
            logger.error("Car stock or sold is required");
            throw new IllegalArgumentException("Car stock or sold is required");
        }

        var authentication = getAuthentication();
        var user = getUser(authentication);
        var car = getCar(id);

        if (carDto.getCarStockSold().equals(CarStockSold.SOLD)) {
            car.setCarStockSold(carDto.getCarStockSold());
            car.setBuyerName(carDto.getBuyerName());
            car.setHandoverDate(carDto.getHandoverDate());
        }
        Car savedCar = carRepository.save(car);
        logger.info("Car updated successfully with id: {}", id);
        return modelMapper.map(savedCar, CarDto.class);
    }

    /**
     * @param pageable - The pageable object to get the cars with pagination. It contains the page number and size.
     * @return - The page object containing the cars fetched. The cars are mapped to display cars.
     * If the cars are not found, it returns an empty list.
     */
    @Override
    public Page<DisplayCarsDto> getAllCars(Pageable pageable) {
        logger.info("Fetching all cars with pagination: page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Car> cars = carRepository.findAll(pageable);
        logger.info("Fetched {} cars out of {}", cars.getNumberOfElements(), cars.getTotalElements());

        Page<DisplayCarsDto> displayCarsDtos = cars.map(car -> {
            DisplayCarsDto displayCarsDto = modelMapper.map(car, DisplayCarsDto.class);
            return displayCarsDto;
        });
        logger.info("Successfully mapped cars to display cars");
        return displayCarsDtos;
    }

    /**
     * @param model - The model of the car to be fetched. It is used to search the cars by model.
     * @param pageable - The pageable object to get the cars with pagination. It contains the page number and size.
     * @return - The page object containing the cars fetched. The cars are mapped to carDto.
     */
    @Override
    public Page<CarDto> getCarsByModel(String model, Pageable pageable) {
        logger.info("Fetching cars by model: {}", model);

        if (model == null || model.trim().isEmpty()) {
            logger.error("Model parameter is null or empty");
            throw new IllegalArgumentException("Model parameter is null or empty");
        }

        Page<Car> cars = carRepository.findByModelContainingIgnoreCase(model, pageable);
        if (cars.isEmpty()) {
            logger.error("No cars found by model: {}", model);
        } else {
            logger.info("Fetched {} cars by model: {}", cars.getNumberOfElements(), model);
        }
        return cars.map(car -> modelMapper.map(car, CarDto.class));
    }

    /**
     * @param id - The id of the car to be deleted. If the car is not found, it throws a CarNotFoundException.
     */
    @Override
    @Transactional
    public void deleteCarById(Long id) {
        logger.info("Deleting car with ID: {}", id);
        var authentication = getAuthentication();
        var user = getUser(authentication);
        var car = getCar(id);

        // TODO: checkSalesPermission(user);
        carRepository.deleteById(id);
        logger.info("Car deleted successfully with ID: {}", id);
    }

    /**
     * @param regNumber - The registration number of the car to be fetched. It is used to search the cars by registration number.
     * @return - The list of carDto objects fetched. If the cars are not found, it returns an empty list.
     * The cars are mapped to carDto.
     */
    @Override
    public List<CarDto> getCarByRegNumber(String regNumber) {
        logger.info("Fetching cars by registration number: {}", regNumber);

        if (regNumber == null || regNumber.trim().isEmpty()) {
            logger.error("Registration number is null or empty");
            throw new IllegalArgumentException("Registration number cannot be null or empty");
        }

        List<Car> cars = carRepository.findByRegNumberContainingIgnoreCase(regNumber);
        if (cars.isEmpty()) {
            logger.error("No cars found by registration number: {}", regNumber);
            throw new CarNotFoundException("No cars found by registration number: " + regNumber);
        }
        logger.info("Fetched {} cars by registration number: {}", cars.size(), regNumber);

        return cars.stream()
                .map(car -> modelMapper.map(car, CarDto.class))
                .collect(Collectors.toList());
    }

    /**
     * @param chassisNumber - The chassis number of the car to be fetched. It is used to search the cars by chassis number.
     * @return - The list of carDto objects fetched. If the cars are not found, it returns an empty list.
     * The cars are mapped to carDto.
     */
    @Override
    public List<CarDto> getCarByChassisNumber(String chassisNumber) {
        logger.info("Fetching cars by chassis number: {}", chassisNumber);

        if (chassisNumber == null || chassisNumber.trim().isEmpty()) {
            logger.error("Chassis number is null or empty");
            throw new IllegalArgumentException("Chassis number cannot be null or empty");
        }

        List<Car> cars = carRepository.findByChassisNumberContainingIgnoreCase(chassisNumber);
        if (cars.isEmpty()) {
            logger.error("No cars found by chassis number: {}", chassisNumber);
            throw new CarNotFoundException("No cars found by chassis number: " + chassisNumber);
        }
        logger.info("Fetched {} cars by chassis number: {}", cars.size(), chassisNumber);

        return cars.stream()
                .map(car -> modelMapper.map(car, CarDto.class))
                .collect(Collectors.toList());
    }

    /**
     * @param buyerName - The buyer name of the car to be fetched. It is used to search the cars by buyer name.
     * @return - The list of carDto objects fetched. If the cars are not found, it returns an empty list.
     */
    @Override
    public List<CarDto> getCarByBuyerName(String buyerName) {
        logger.info("Fetching cars by buyer name: {}", buyerName);

        if (buyerName == null || buyerName.trim().isEmpty()) {
            logger.error("Buyer name is null or empty");
            throw new IllegalArgumentException("Buyer name cannot be null or empty");
        }

        List<Car> car = carRepository.findByBuyerNameContainingIgnoreCase(buyerName);
        if (car.isEmpty()) {
            logger.error("No cars found by buyer name: {}", buyerName);
            throw new CarNotFoundException("No cars found by buyer name: " + buyerName);
        }
        logger.info("Fetched car by buyer name: {}", buyerName);

        return car.stream()
                .map(c -> modelMapper.map(c, CarDto.class))
                .collect(Collectors.toList());
    }
}
