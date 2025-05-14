package com.check_location.location_ddd.adapter.in.web;


import com.check_location.location_ddd.application.service.RentalService;
import com.check_location.location_ddd.domain.model.Rental;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rental API", description = "Opérations de location de véhicules")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    @Operation(summary = "Louer un véhicule", description = "Crée une location de véhicule si toutes les règles sont respectées")
    public ResponseEntity<Rental> rentCar(@RequestBody Rental rental) {
        Rental createdRental = rentalService.rentCar(rental);
        return ResponseEntity.ok(createdRental);
    }

    @PatchMapping("/{id}/return")
    @Operation(summary = "Restituer un véhicule", description = "Met à jour la location avec la date de retour et applique les pénalités si nécessaire")
    public ResponseEntity<Rental> returnCar(@PathVariable UUID id) {
        LocalDate actualReturnDate = LocalDate.now();
        Rental returnedRental = rentalService.returnCar(id, actualReturnDate);
        return ResponseEntity.ok(returnedRental);
    }
}
