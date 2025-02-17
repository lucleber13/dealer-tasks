package com.cbcode.dealertasks.Cars.repository;

import com.cbcode.dealertasks.Cars.model.Car;
import com.cbcode.dealertasks.Cars.model.Enums.CarStockSold;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("SELECT c FROM Car c WHERE LOWER(c.regNumber) LIKE LOWER(CONCAT('%', :regNumber, '%'))")
    List<Car> findByRegNumberContainingIgnoreCase(@Param("regNumber") String regNumber);

    @Query("SELECT c FROM Car c WHERE LOWER(c.chassisNumber) LIKE LOWER(CONCAT('%', :chassisNumber, '%'))")
    List<Car> findByChassisNumberContainingIgnoreCase(@Param("chassisNumber") String chassisNumber);

    @Query("SELECT c FROM Car c WHERE LOWER(c.model) LIKE LOWER(CONCAT('%', :model, '%'))")
    Page<Car> findByModelContainingIgnoreCase(@Param("model") String model, Pageable pageable);

    @Query("SELECT c FROM Car c WHERE LOWER(c.buyerName) LIKE LOWER(CONCAT('%', :buyerName, '%'))")
    List<Car> findByBuyerNameContainingIgnoreCase(@Param("buyerName") String buyerName);

    @Query("SELECT c FROM Car c WHERE c.carStockSold = :carStockSold")
    Page<Car> findAllByCarStockSold(@Param("carStockSold") CarStockSold carStockSold, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Car e WHERE e.regNumber = :regNumber")
    boolean existsByRegNumber(@Param("regNumber") String regNumber);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Car e WHERE e.chassisNumber = :chassisNumber")
    boolean existsByChassisNumber(@Param("chassisNumber") String chassisNumber);

}
