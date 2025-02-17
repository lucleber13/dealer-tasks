package com.cbcode.dealertasks.Cars.service;

import com.cbcode.dealertasks.Cars.model.DTOs.CarDto;
import com.cbcode.dealertasks.Cars.model.DTOs.DisplayCarsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CarService {
    CarDto createCar(CarDto carDto);

    CarDto getCarById(Long id);

    CarDto updateCarToSold(Long id, CarDto carDto);

    Page<DisplayCarsDto> getAllCars(Pageable pageable);

    Page<CarDto> getCarsByModel(String model, Pageable pageable);

    void deleteCarById(Long id);

    List<CarDto> getCarByRegNumber(String regNumber);

    List<CarDto> getCarByChassisNumber(String chassisNumber);

    List<CarDto> getCarByBuyerName(String buyerName);
}
