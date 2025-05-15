package com.check.location.service;

import com.check.location.model.Car;
import com.check.location.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    private CarRepository carRepository;
    private CarService carService;

    @BeforeEach
    void setUp() {
        carRepository = mock(CarRepository.class);
        carService = new CarService(carRepository);
    }

    @Test
    void shouldCreateCarSuccessfully() {
        Car newCar = new Car();
        newCar.setModel("Peugeot 208");

        Car savedCar = new Car();
        savedCar.setId(UUID.randomUUID());
        savedCar.setModel("Peugeot 208");

        when(carRepository.save(newCar)).thenReturn(savedCar);

        Car result = carService.createCar(newCar);

        assertNotNull(result);
        assertEquals("Peugeot 208", result.getModel());
        verify(carRepository).save(newCar);
    }

    @Test
    void shouldReturnCarById() {
        UUID carId = UUID.randomUUID();
        Car car = new Car();
        car.setId(carId);
        car.setModel("Renault Clio");

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        Car result = carService.getCarById(carId);

        assertNotNull(result);
        assertEquals("Renault Clio", result.getModel());
        verify(carRepository).findById(carId);
    }

    @Test
    void shouldThrowExceptionIfCarNotFoundById() {
        UUID carId = UUID.randomUUID();
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            carService.getCarById(carId);
        });

        assertEquals("Car not found with ID: " + carId, exception.getMessage());
    }

    @Test
    void shouldReturnAllCars() {
        Car car1 = new Car();
        car1.setId(UUID.randomUUID());
        car1.setModel("Toyota");
        Car car2 = new Car();
        car2.setId(UUID.randomUUID());
        car2.setModel("Nissan");

        when(carRepository.findAll()).thenReturn(List.of(car1, car2));

        List<Car> result = carService.getAllCars();

        assertEquals(2, result.size());
        verify(carRepository).findAll();
    }

    @Test
    void shouldReturnCarsThatNeedInspection() {
        Car car1 = new Car();
        car1.setMileage(200000);
        car1.setLastInspectionMileage(40000);
        car1.setLastInspectionDate(LocalDate.now().minusDays(100));
        car1.setConsecutiveRentalDays(30);

        Car car2 = new Car();
        car2.setMileage(10000);
        car2.setLastInspectionMileage(5000);
        car2.setLastInspectionDate(LocalDate.now().minusDays(10));
        car2.setConsecutiveRentalDays(70);

        Car car3 = new Car();
        car3.setMileage(80000);
        car3.setLastInspectionMileage(70000);
        car3.setLastInspectionDate(LocalDate.now().minusDays(20));
        car3.setConsecutiveRentalDays(20);

        when(carRepository.findAll()).thenReturn(List.of(car1, car2, car3));

        List<Car> result = carService.getCarsToInspect();

        assertEquals(2, result.size());
        assertTrue(result.contains(car1));
        assertTrue(result.contains(car2));
        assertFalse(result.contains(car3));
    }
}
