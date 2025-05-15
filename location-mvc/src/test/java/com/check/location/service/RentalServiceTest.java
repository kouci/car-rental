package com.check.location.service;



import com.check.location.enums.CarStatus;
import com.check.location.model.Car;
import com.check.location.model.Client;
import com.check.location.model.Rental;
import com.check.location.repository.CarRepository;
import com.check.location.repository.ClientRepository;
import com.check.location.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalServiceTest {

    private RentalRepository rentalRepository;
    private ClientRepository clientRepository;
    private CarRepository carRepository;
    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        rentalRepository = mock(RentalRepository.class);
        clientRepository = mock(ClientRepository.class);
        carRepository = mock(CarRepository.class);
        rentalService = new RentalService(rentalRepository, clientRepository, carRepository);
    }

    @Test
    void shouldRentCarSuccessfully() {
        UUID clientId = UUID.randomUUID();
        UUID carId = UUID.randomUUID();
        Rental rental = new Rental();
        rental.setClientId(clientId);
        rental.setCarId(carId);
        rental.setStartDate(LocalDate.now());
        rental.setEndDate(LocalDate.now().plusDays(5));

        Client client = new Client();
        client.setId(clientId);
        client.setHasUnpaidDebt(false);
        client.setHasBlockedDeposit(false);

        Car car = new Car();
        car.setId(carId);
        car.setStatus(CarStatus.AVAILABLE);
        car.setOutOfService(false);
        car.setConsecutiveRentalDays(0);
        car.setMileage(50000);
        car.setLastInspectionMileage(10000);
        car.setLastInspectionDate(LocalDate.now().minusDays(10));
        car.setModel("Toyota");

        when(clientRepository.findClientById(clientId)).thenReturn(Optional.of(client));
        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(rentalRepository.findByClientId(clientId)).thenReturn(Collections.emptyList());
        when(rentalRepository.findByCarId(carId)).thenReturn(Optional.empty());
        when(carRepository.findAll()).thenReturn(List.of(car));
        when(rentalRepository.save(rental)).thenReturn(rental);

        Rental result = rentalService.rentCar(rental);
        assertNotNull(result);
        verify(carRepository).save(car);
        verify(rentalRepository).save(rental);
    }

    @Test
    void shouldThrowExceptionIfClientHasUnpaidDebt() {
        UUID clientId = UUID.randomUUID();
        UUID carId = UUID.randomUUID();
        Rental rental = new Rental();
        rental.setClientId(clientId);
        rental.setCarId(carId);
        rental.setStartDate(LocalDate.now());
        rental.setEndDate(LocalDate.now().plusDays(5));

        Client client = new Client();
        client.setId(clientId);
        client.setHasUnpaidDebt(true);
        client.setHasBlockedDeposit(false);

        Car dummyCar = new Car();
        dummyCar.setId(carId);
        dummyCar.setOutOfService(false);
        dummyCar.setMileage(10000);
        dummyCar.setLastInspectionMileage(5000);
        dummyCar.setLastInspectionDate(LocalDate.now());
        dummyCar.setConsecutiveRentalDays(0);
        dummyCar.setModel("Peugeot");

        when(clientRepository.findClientById(clientId)).thenReturn(Optional.of(client));
        when(carRepository.findById(carId)).thenReturn(Optional.of(dummyCar));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rentalService.rentCar(rental);
        });

        assertEquals("Client has unpaid debt or blocked deposit", exception.getMessage());
    }

}
