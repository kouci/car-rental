package com.check.location.repository;

import com.check.location.model.Rental;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class RentalRepository {
    private final Map<UUID, Rental> rentals = new ConcurrentHashMap<>();


    public Rental save(Rental rental) {
        if (rental.getId() == null) {
            rental.setId(UUID.randomUUID());
        }
        rentals.put(rental.getId(), rental);
        return rental;
    }

    public List<Rental> findAll() {
        return new ArrayList<>(rentals.values());
    }

    public List<Rental> findByClientId(UUID clientId) {
        return rentals.values().stream()
                .filter(r -> r.getClientId().equals(clientId))
                .toList();
    }

    public Optional<Rental> findById(UUID rentalId) {
        return Optional.ofNullable(rentals.get(rentalId));
    }

    public Optional<Rental> findByCarId(UUID carId) {
        return rentals.values().stream()
                .filter(r -> r.getCarId().equals(carId))
                .findFirst();
    }
}
