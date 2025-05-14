package com.check_location.location_ddd.application.service;

import com.check_location.location_ddd.domain.model.Car;
import com.check_location.location_ddd.port.out.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CarService {

    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car createCar(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        if (car.getId() != null) {
            throw new IllegalArgumentException("New car must not have an ID");
        }
        return carRepository.save(car);
    }

    public Car getCarById(UUID id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found with ID: " + id));
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> getCarsToInspect() {
        LocalDate now = LocalDate.now();

        return carRepository.findAll().stream()
                .filter(car -> {
                    boolean needsInspectionByMileage = car.getMileage() - car.getLastInspectionMileage() > 150_000;
                    boolean needsInspectionByTime = car.getLastInspectionDate() != null
                            && car.getLastInspectionDate().isBefore(now.minusDays(90));
                    boolean needsInspectionByConsecutiveRental = car.getConsecutiveRentalDays() > 60;

                    return needsInspectionByMileage || needsInspectionByTime || needsInspectionByConsecutiveRental;
                })
                .toList();
    }
}
