package com.check.location.controller;

import com.check.location.model.Car;
import com.check.location.repository.CarRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    @Test
    void testCreateCar_shouldReturnCreatedCar() throws Exception {
        Car car = new Car();
        car.setBrand("Tesla");
        car.setModel("Model 3");
        car.setLastInspectionDate(LocalDate.now().minusMonths(2));

        mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.brand").value("Tesla"))
                .andExpect(jsonPath("$.model").value("Model 3"));
    }

    @Test
    void testGetCarById_shouldReturnCar() throws Exception {
        Car savedCar = new Car();
        savedCar.setBrand("Ford");
        savedCar.setModel("Mustang");
        savedCar.setLastInspectionDate(LocalDate.now().minusMonths(3));
        savedCar = carRepository.save(savedCar);

        mockMvc.perform(get("/api/cars/" + savedCar.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCar.getId().toString()))
                .andExpect(jsonPath("$.brand").value("Ford"))
                .andExpect(jsonPath("$.model").value("Mustang"));
    }
}
