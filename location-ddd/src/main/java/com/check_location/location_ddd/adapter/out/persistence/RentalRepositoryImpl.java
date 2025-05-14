package com.check_location.location_ddd.adapter.out.persistence;

import com.check_location.location_ddd.domain.model.Rental;
import com.check_location.location_ddd.port.out.RentalRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class RentalRepositoryImpl implements RentalRepository {

    private final Map<UUID, Rental> rentals = new ConcurrentHashMap<>();

    @Override
    public Rental save(Rental rental) {
        if (rental.getId() == null) {
            rental.setId(UUID.randomUUID());
        }
        rentals.put(rental.getId(), rental);
        return rental;
    }

    @Override
    public List<Rental> findAll() {
        return new ArrayList<>(rentals.values());
    }

    @Override
    public List<Rental> findByClientId(UUID clientId) {
        return rentals.values().stream()
                .filter(r -> r.getClientId().equals(clientId))
                .toList();
    }

    @Override
    public Optional<Rental> findById(UUID rentalId) {
        return Optional.ofNullable(rentals.get(rentalId));
    }

    @Override
    public Optional<Rental> findByCarId(UUID carId) {
        return rentals.values().stream()
                .filter(r -> r.getCarId().equals(carId))
                .findFirst();
    }
}
