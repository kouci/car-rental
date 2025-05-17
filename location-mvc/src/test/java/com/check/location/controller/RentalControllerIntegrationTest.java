package com.check.location.controller;

import com.check.location.enums.CarStatus;
import com.check.location.model.Car;
import com.check.location.model.Client;
import com.check.location.model.Rental;
import com.check.location.repository.CarRepository;
import com.check.location.repository.ClientRepository;
import com.check.location.repository.RentalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RentalControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private RentalRepository rentalRepository;
    @Autowired private CarRepository carRepository;
    @Autowired private ClientRepository clientRepository;

    private final LocalDate today = LocalDate.of(2025, 5, 17);
    private final LocalDate threeDaysLater = today.plusDays(3);
    private final LocalDate fiveDaysAgo = today.minusDays(5);
    private final LocalDate ninetyOneDaysAgo = today.minusDays(91);



    private Client createClient(String name, boolean unpaidDebt, boolean blockedDeposit) {
        Client client = new Client();
        client.setName(name);
        client.setEmail(name + "@example.com");
        client.setHasUnpaidDebt(unpaidDebt);
        client.setHasBlockedDeposit(blockedDeposit);
        return clientRepository.save(client);
    }

    private Car createCar(String model, boolean outOfService, int mileage, LocalDate lastInspectionDate, int consecutiveDays, CarStatus status) {
        Car car = new Car();
        car.setModel(model);
        car.setBrand("Brand");
        car.setOutOfService(outOfService);
        car.setMileage(mileage);
        car.setLastInspectionDate(lastInspectionDate);
        car.setLastInspectionMileage(0);
        car.setConsecutiveRentalDays(consecutiveDays);
        car.setStatus(status);
        return carRepository.save(car);
    }

    private Rental createRental(Client client, Car car, LocalDate start, LocalDate end) {
        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car.getId());
        rental.setStartDate(start);
        rental.setEndDate(end);
        return rentalRepository.save(rental);
    }

    private void performRentalAndExpectBadRequest(Rental rental, String expectedMessage) throws Exception {
        mockMvc.perform(post("/api/rentals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(rental)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(expectedMessage)));
    }

    @Test void shouldRejectIfClientHasTwoActiveRentals() throws Exception {
        Client client = createClient("TwoActive", false, false);
        Car car1 = createCar("M1", false, 10000, today, 0, CarStatus.AVAILABLE);
        Car car2 = createCar("M2", false, 10000, today, 0, CarStatus.AVAILABLE);
        Car car3 = createCar("M3", false, 10000, today, 0, CarStatus.AVAILABLE);
        createRental(client, car1, today, today.plusDays(2));
        createRental(client, car2, today, today.plusDays(3));

        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car3.getId());
        rental.setStartDate(today);
        rental.setEndDate(today.plusDays(3));

        performRentalAndExpectBadRequest(rental, "Client already has 2 active rentals");
    }

    @Test void shouldRejectIfCarInMaintenance() throws Exception {
        Client client = createClient("ClientMaint", false, false);
        Car car = createCar("Toyota", false, 10000, today, 0, CarStatus.MAINTENANCE);

        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car.getId());
        rental.setStartDate(today);
        rental.setEndDate(today.plusDays(3));

        performRentalAndExpectBadRequest(rental, "Car is out of service");
    }

    @Test void shouldRejectIfCarAlreadyRented() throws Exception {
        Client client = createClient("ClientRent", false, false);
        Car car = createCar("Toyota", false, 10000, today, 0, CarStatus.AVAILABLE);
        createRental(client, car, today, today.plusDays(3));

        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car.getId());
        rental.setStartDate(today);
        rental.setEndDate(today.plusDays(3));

        performRentalAndExpectBadRequest(rental, "Car is already rented");
    }

    @Test void shouldRejectIfCarOver150000KmWithoutInspection() throws Exception {
        Client client = createClient("ClientKM", false, false);
        Car car = createCar("Toyota", false, 160000, today.minusDays(10), 0, CarStatus.AVAILABLE);

        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car.getId());
        rental.setStartDate(today);
        rental.setEndDate(today.plusDays(3));

        performRentalAndExpectBadRequest(rental, "Car requires technical inspection due to mileage");
    }

    @Test void shouldRejectIfRentalTooShortOrTooLong() throws Exception {
        Client client = createClient("ClientDuration", false, false);
        Car car = createCar("Toyota", false, 10000, today, 0, CarStatus.AVAILABLE);

        Rental tooShort = new Rental();
        tooShort.setClientId(client.getId());
        tooShort.setCarId(car.getId());
        tooShort.setStartDate(today);
        tooShort.setEndDate(today);
        performRentalAndExpectBadRequest(tooShort, "Rental duration must be between 1 and 30 days");

        Rental tooLong = new Rental();
        tooLong.setClientId(client.getId());
        tooLong.setCarId(car.getId());
        tooLong.setStartDate(today);
        tooLong.setEndDate(today.plusDays(40));
        performRentalAndExpectBadRequest(tooLong, "Rental duration must be between 1 and 30 days");
    }

    @Test void shouldRejectIfConsecutiveDaysExceed60() throws Exception {
        Client client = createClient("ClientCons60", false, false);
        Car car = createCar("Toyota", false, 10000, today, 61, CarStatus.AVAILABLE);

        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car.getId());
        rental.setStartDate(today);
        rental.setEndDate(today.plusDays(3));

        performRentalAndExpectBadRequest(rental, "Car cannot be rented more than 60 consecutive days without inspection");
    }

    @Test void shouldRejectIfLastInspectionOver90Days() throws Exception {
        Client client = createClient("ClientInspect90", false, false);
        Car car = createCar("Toyota", false, 10000, ninetyOneDaysAgo, 0, CarStatus.AVAILABLE);

        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car.getId());
        rental.setStartDate(today);
        rental.setEndDate(today.plusDays(3));

        performRentalAndExpectBadRequest(rental, "Car requires technical inspection (90-day rule)");
    }

    @Test void shouldRejectIfSameModelRentedInLast15Days() throws Exception {
        Client client = createClient("ClientSameModel", false, false);
        Car car1 = createCar("Toyota", false, 10000, today, 0, CarStatus.AVAILABLE);
        Car car2 = createCar("Toyota", false, 10000, today, 0, CarStatus.AVAILABLE);
        createRental(client, car1, today.minusDays(10), today.plusDays(2));

        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car2.getId());
        rental.setStartDate(today);
        rental.setEndDate(today.plusDays(3));

        performRentalAndExpectBadRequest(rental, "Client already rented a similar vehicle in the last 15 days");
    }

    @Test void shouldRejectIfClientHasUnpaidDebtOrBlockedDeposit() throws Exception {
        Client client = createClient("ClientDebt", true, true);
        Car car = createCar("Toyota", false, 10000, today, 0, CarStatus.AVAILABLE);

        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car.getId());
        rental.setStartDate(today);
        rental.setEndDate(today.plusDays(3));

        performRentalAndExpectBadRequest(rental, "Client has unpaid debt or blocked deposit");
    }

    @Test void shouldApplyPenaltyIfReturnIsLateOver3Days() throws Exception {
        Client client = createClient("ClientLate", false, false);
        Car car = createCar("Toyota", false, 10000, today, 0, CarStatus.AVAILABLE);
        Rental rental = createRental(client, car, fiveDaysAgo, today.minusDays(4));

        mockMvc.perform(patch("/api/rentals/" + rental.getId() + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.penaltyApplied").value(true));
    }
}
