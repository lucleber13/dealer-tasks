package com.cbcode.dealertasks.Cars.controller;

import com.cbcode.dealertasks.Cars.model.DTOs.CarDto;
import com.cbcode.dealertasks.Cars.model.DTOs.DisplayCarsDto;
import com.cbcode.dealertasks.Cars.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
@CrossOrigin(origins = "*") // This is a temporary solution to allow requests from any origin
public class CarController {
    private static final Logger logger = LoggerFactory.getLogger(CarController.class);
    private final CarService carService;


    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Create a new car in the database with the details of the car.
     *
     * @param carDto - the car details to be created in the database.
     * @return ResponseEntity<CarDto> - the car created successfully with the details of the car. The response is in JSON format.
     * @see CarDto for more details.
     */
    @Operation(summary = "Create a new car in the database with the details of the car.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Car created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))}),
            @ApiResponse(responseCode = "400", description = "Car not created",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content)
    })
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    public ResponseEntity<CarDto> createCar(@RequestBody CarDto carDto) {
        logger.info("Successfully created a car.");
        return ResponseEntity.ok(carService.createCar(carDto));
    }

    /**
     * Update the details of a car in the database with the details of the car.
     *
     * @param id - the id of the car to be updated in the database.
     * @return ResponseEntity<CarDto> - the car updated successfully with the details of the car. The response is in JSON format.
     * @see CarDto for more details.
     */
    @Operation(summary = "Get a car by id.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car returned successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))}),
            @ApiResponse(responseCode = "400", description = "Car not updated",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content)
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    public ResponseEntity<CarDto> getCarById(@PathVariable(name = "id") Long id) {
        logger.info("Successfully retrieved a car by id.");
        return ResponseEntity.ok(carService.getCarById(id));
    }

    /**
     * Update the details of a car in the database with the details of the car.
     *
     * @param id     - the id of the car to be updated in the database.
     * @param carDto - the car details to be updated in the database.
     * @return ResponseEntity<CarDto> - the car updated successfully with the details of the car. The response is in JSON format.
     * @see CarDto for more details.
     */
    @Operation(summary = "Update the details of a car in the database with the details of the car.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Car updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))}),
            @ApiResponse(responseCode = "400", description = "Car not updated",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content)
    })
    @PutMapping(value = "/update-sold/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    public ResponseEntity<CarDto> updateCarStockSold(@PathVariable(name = "id") Long id, @RequestBody CarDto carDto) {
        logger.info("Successfully updated the car stock to sold.");
        return ResponseEntity.ok(carService.updateCarToSold(id, carDto));
    }

    /**
     * Get all cars in the database with pagination and sorting options available to the user based on the role of the user,
     * making the request to the endpoint.
     *
     * @param pageNr   - the page number to be displayed (default value is 0)
     * @param pageSize - the number of items to be displayed on a page (default value is 10)
     * @param sortBy   - the field to sort the items by (default value is id)
     * @return ResponseEntity<Page<DisplayCarsDto>> -
     * A page of cars to be displayed to the user on the front end with the details of the cars in the database.
     */
    @Operation(summary = "Get all cars in the database with pagination and sorting options available to the user based on the role of the user making the request to the endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A page of cars to be displayed to the user on the front end with the details of the cars in the database.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DisplayCarsDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content)
    })
    @GetMapping(value = "/all", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    public ResponseEntity<Page<DisplayCarsDto>> getAllCars(@RequestParam(defaultValue = "0") Integer pageNr,
                                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                                           @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(pageNr, pageSize, Sort.by(sortBy));
        return ResponseEntity.ok(carService.getAllCars(pageable));
    }

    /**
     * Get cars by model in the database with pagination and sorting options available to the user based on the role of the user,
     * making the request to the endpoint.
     *
     * @param model    - the model of the car to be displayed in the database (default value is "")
     * @param pageNr   - the page number to be displayed (default value is 0)
     * @param pageSize - the number of items to be displayed on a page (default value is 10)
     * @param sortBy   - the field to sort the items by (default value is id)
     * @return ResponseEntity<Page<CarDto>> -
     * A page of cars to be displayed to the user on the front end with the details of the cars in the database.
     */
    @Operation(summary = "Get cars by model in the database with pagination and sorting options available to the user based on the role of the user making the request to the endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A page of cars to be displayed to the user on the front end with the details of the cars in the database.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content)
    })
    @GetMapping(value = "/car-by-model/{model}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    public ResponseEntity<Page<CarDto>> getCarsByModel(@PathVariable(name = "model") String model,
                                                       @RequestParam(defaultValue = "0") Integer pageNr,
                                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                                       @RequestParam(defaultValue = "id") String sortBy
    ) {
        Pageable pageable = PageRequest.of(pageNr, pageSize, Sort.by(sortBy));
        return ResponseEntity.ok(carService.getCarsByModel(model, pageable));
    }

    /**
     * Delete a car in the database with the details of the car.
     *
     * @param id - the id of the car to be deleted in the database.
     * @return ResponseEntity<Void> - the car deleted successfully with the details of the car. The response is in JSON format.
     */
    @Operation(summary = "Delete a car in the database with the details of the car.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Car deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Car not deleted",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content)
    })
    @DeleteMapping(value = "/delete/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    public ResponseEntity<Void> deleteCarById(@PathVariable(name = "id") Long id) {
        carService.deleteCarById(id);
        logger.info("Successfully deleted a car by id.");
        return ResponseEntity.noContent().build();
    }

    /**
     * Get cars by registration number in the database with pagination and sorting options available to the user based on the role of the user,
     * making the request to the endpoint.
     *
     * @param regNumber - the registration number of the car to be displayed in the database (default value is "")
     * @return ResponseEntity<List<CarDto>> -
     * A list of cars to be displayed to the user on the front end with the details of the cars in the database.
     */
    @Operation(summary = "Get cars by registration number in the database with pagination and sorting options available to the user based on the role of the user making the request to the endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A list of cars to be displayed to the user on the front end with the details of the cars in the database.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content)
    })
    @GetMapping(value = "/reg-number/{regNumber}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    public ResponseEntity<List<CarDto>> getCarByRegNumber(@PathVariable(name = "regNumber") String regNumber) {
        logger.info("Successfully retrieved a car by registration number.");
        return ResponseEntity.ok(carService.getCarByRegNumber(regNumber));
    }

    /**
     * Get cars by chassis number in the database with pagination and sorting options available to the user based on the role of the user,
     * making the request to the endpoint.
     *
     * @param chassisNumber - the chassis number of the car to be displayed in the database (default value is "")
     * @return ResponseEntity<List<CarDto>> -
     * A list of cars to be displayed to the user on the front end with the details of the cars in the database.
     */
    @Operation(summary = "Get cars by chassis number in the database with pagination and sorting options available to the user based on the role of the user making the request to the endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A list of cars to be displayed to the user on the front end with the details of the cars in the database.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content)
    })
    @GetMapping(value = "/chassis-number/{chassisNumber}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    public ResponseEntity<List<CarDto>> getCarByChassisNumber(@PathVariable(name = "chassisNumber") String chassisNumber) {
        logger.info("Successfully retrieved a car by chassis number.");
        return ResponseEntity.ok(carService.getCarByChassisNumber(chassisNumber));
    }

    /**
     * Get cars by buyer name in the database with pagination and sorting options available to the user based on the role of the user,
     * making the request to the endpoint.
     *
     * @param buyerName - the buyer name of the car to be displayed in the database (default value is "")
     * @return ResponseEntity<CarDto> -
     * A car to be displayed to the user on the front end with the details of the car in the database.
     */
    @Operation(summary = "Get cars by buyer name in the database with pagination and sorting options available to the user based on the role of the user making the request to the endpoint.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A car to be displayed to the user on the front end with the details of the car in the database.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CarDto.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = @Content)
    })
    @GetMapping(value = "/buyer-name/{buyerName}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SALES')")
    public ResponseEntity<List<CarDto>> getCarByBuyerName(@PathVariable(name = "buyerName") String buyerName) {
        logger.info("Successfully retrieved a car by buyer name.");
        return ResponseEntity.ok(carService.getCarByBuyerName(buyerName));
    }
}
