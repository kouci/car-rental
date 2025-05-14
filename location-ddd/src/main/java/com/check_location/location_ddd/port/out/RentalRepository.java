package com.check_location.location_ddd.port.out;


import com.check_location.location_ddd.domain.model.Rental;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalRepository {
    Rental save(Rental rental);
    List<Rental> findAll();
    List<Rental> findByClientId(UUID clientId);
    Optional<Rental> findById(UUID rentalId);
    Optional<Rental> findByCarId(UUID carId);
}