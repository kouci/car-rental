package com.check.location.service;

import com.check.location.model.Car;
import com.check.location.repository.CarRepository;
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
        return carRepository.save(car);
    }

    public Car getCarById(UUID id) {
        return carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found with ID: " + id));
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> getCarsToInspect() {
        LocalDate now = LocalDate.now();

        return carRepository.findAll().stream()
                .filter(car -> {
                    boolean needsInspectionByMileage = car.getMileage() - car.getLastInspectionMileage() > 150000;
                    boolean needsInspectionByTime = car.getLastInspectionDate() != null && car.getLastInspectionDate().isBefore(now.minusDays(90));
                    boolean needsInspectionByConsecutiveRental = car.getConsecutiveRentalDays() > 60;
                    return needsInspectionByMileage || needsInspectionByTime || needsInspectionByConsecutiveRental;
                })
                .toList();
    }
}
