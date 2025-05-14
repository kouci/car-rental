package com.check.location.repository;

import com.check.location.model.Car;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class CarRepository {

    private final ConcurrentMap<UUID, Car> cars = new ConcurrentHashMap<>();

    public Car save(Car car) {
        if (car == null) {
            throw new IllegalArgumentException("Car cannot be null");
        }
        if (car.getId() == null) {
            car.setId(UUID.randomUUID());
        }
        cars.put(car.getId(), car);
        return car;
    }

    public Optional<Car> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return  Optional.ofNullable(cars.get(id));
    }

    public List<Car> findAll() {
        return List.copyOf(cars.values());
    }

    public boolean existsById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return cars.containsKey(id);
    }


}