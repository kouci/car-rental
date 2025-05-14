package com.check.location.controller;

import com.check.location.model.Rental;
import com.check.location.service.RentalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rental API", description = "Opérations de location de véhicules")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Louer un véhicule", description = "Crée une location de véhicule si toutes les règles sont respectées")
    @PostMapping
    public ResponseEntity<Rental> rentCar(@RequestBody Rental rental) {
        return ResponseEntity.ok(rentalService.rentCar(rental));
    }


    @PatchMapping("/{id}/return")
    @Operation(
            summary = "Restituer un véhicule",
            description = "Met à jour la location avec la date de retour et applique les pénalités si nécessaire"
    )
    public ResponseEntity<Rental> returnCar(@PathVariable UUID id) {
        LocalDate actualReturnDate = LocalDate.now();
        Rental returnedRental = rentalService.returnCar(id, actualReturnDate);
        return ResponseEntity.ok(returnedRental);
    }


}
