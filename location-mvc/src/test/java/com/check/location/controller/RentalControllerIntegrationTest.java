package com.check.location.controller;

import com.check.location.model.Car;
import com.check.location.model.Client;
import com.check.location.model.Rental;
import com.check.location.repository.CarRepository;
import com.check.location.repository.ClientRepository;
import com.check.location.repository.RentalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ClientRepository clientRepository;


    private Client createClient(String name, String email) {
        Client client = new Client();
        client.setName(name);
        client.setEmail(email);
        return clientRepository.save(client);
    }


    private Car createCar(String brand, String model) {
        Car car = new Car();
        car.setBrand(brand);
        car.setModel(model);
        car.setLastInspectionDate(LocalDate.now().minusMonths(1));
        return carRepository.save(car);
    }


    private Rental createRental(Client client, Car car, LocalDate startDate, LocalDate endDate) {
        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car.getId());
        rental.setStartDate(startDate);
        rental.setEndDate(endDate);
        return rentalRepository.save(rental);
    }

    @Test
    void testRentCar() throws Exception {
        Client client = createClient("tuto tuto", "tuto@example.com");
        Car car = createCar("Toyota", "Corolla");
        Rental rental = new Rental();
        rental.setClientId(client.getId());
        rental.setCarId(car.getId());
        rental.setStartDate(LocalDate.now());
        rental.setEndDate(LocalDate.now().plusDays(3));

        mockMvc.perform(post("/api/rentals")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(rental)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testReturnCar_withExistingRental_shouldUpdateActualReturnDate() throws Exception {
        Client client = createClient("Test Client", "testclient@example.com");
        Car car = createCar("Honda", "Civic");
        Rental rental = createRental(client, car, LocalDate.now().minusDays(5), LocalDate.now().plusDays(1));

        mockMvc.perform(patch("/api/rentals/" + rental.getId() + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualReturnDate").value(LocalDate.now().toString()));
    }

}
