package com.check_location.location_ddd.port.out;

import com.check_location.location_ddd.domain.model.Car;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarRepository {
    Car save(Car car);
    Optional<Car> findById(UUID id);
    List<Car> findAll();
    boolean existsById(UUID id);
}