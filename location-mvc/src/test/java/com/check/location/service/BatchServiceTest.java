package com.check.location.service;

import com.check.location.enums.CarStatus;
import com.check.location.model.Car;
import com.check.location.model.Rental;
import com.check.location.repository.CarRepository;
import com.check.location.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class BatchServiceTest {

    private RentalRepository rentalRepository;
    private CarRepository carRepository;
    private BatchService batchService;

    @BeforeEach
    void setUp() {
        rentalRepository = mock(RentalRepository.class);
        carRepository = mock(CarRepository.class);
        batchService = new BatchService(rentalRepository, carRepository);
    }

    @Test
    void shouldLogLateRentals() {
        Rental rental = new Rental();
        rental.setId(UUID.randomUUID());
        rental.setEndDate(LocalDate.now().minusDays(5));

        when(rentalRepository.findAll()).thenReturn(List.of(rental));

        batchService.executeBatch();


        verify(rentalRepository, times(1)).findAll();
    }

    @Test
    void shouldSetCarToMaintenanceIfMissedInspectionsAreHigh() {
        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setMissedInspections(3);
        car.setStatus(CarStatus.AVAILABLE);
        car.setLastInspectionDate(LocalDate.now().minusDays(100));

        when(carRepository.findAll()).thenReturn(List.of(car));

        batchService.executeBatch();

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(carRepository, atLeastOnce()).save(carCaptor.capture());

        Car updatedCar = carCaptor.getValue();
        assertEquals(CarStatus.MAINTENANCE, updatedCar.getStatus());
    }


    @Test
    void shouldLogCarsForUpcomingInspection() {
        Car car = new Car();
        car.setId(UUID.randomUUID());
        car.setLastInspectionDate(LocalDate.now().minusDays(90));

        when(carRepository.findAll()).thenReturn(List.of(car));

        batchService.executeBatch();

        verify(carRepository, atLeastOnce()).findAll();
    }
}
