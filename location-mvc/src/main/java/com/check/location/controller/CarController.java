package com.check.location.controller;

import com.check.location.model.Car;
import com.check.location.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car API", description = "Opérations liées aux voitures")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "Créer une nouvelle voiture", description = "Crée une voiture et la sauvegarde dans le système")
    @ApiResponse(responseCode = "200", description = "Voiture créée avec succès")
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        return ResponseEntity.ok(carService.createCar(car));
    }

    @Operation(summary = "Récupérer une voiture par ID", description = "Retourne une voiture en fonction de son UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voiture trouvée"),
            @ApiResponse(responseCode = "404", description = "Voiture non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable UUID id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @Operation(summary = "Lister toutes les voitures", description = "Retourne la liste complète des voitures enregistrées")
    @ApiResponse(responseCode = "200", description = "Liste des voitures récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }
    @Operation(summary = "Lister les voitures à inspecter", description = "Retourne les voitures nécessitant une inspection technique")
    @ApiResponse(responseCode = "200", description = "Liste des voitures à inspecter")
    @GetMapping("/inspection")
    public ResponseEntity<List<Car>> getCarsToInspect() {
        return ResponseEntity.ok(carService.getCarsToInspect());
    }
}
