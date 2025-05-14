package com.check_location.location_ddd.adapter.out.persistence;

import com.check_location.location_ddd.domain.model.Car;
import com.check_location.location_ddd.port.out.CarRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class CarRepositoryImpl implements CarRepository {

    private final ConcurrentMap<UUID, Car> cars = new ConcurrentHashMap<>();

    @Override
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

    @Override
    public Optional<Car> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return Optional.ofNullable(cars.get(id));
    }

    @Override
    public List<Car> findAll() {
        return List.copyOf(cars.values());
    }

    @Override
    public boolean existsById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return cars.containsKey(id);
    }
}