package com.check_location.location_ddd.adapter.in.web;

import com.check_location.location_ddd.application.service.CarService;
import com.check_location.location_ddd.domain.model.Car;
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
        Car createdCar = carService.createCar(car);
        return ResponseEntity.ok(createdCar);
    }

    @Operation(summary = "Récupérer une voiture par ID", description = "Retourne une voiture en fonction de son UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Voiture trouvée"),
            @ApiResponse(responseCode = "404", description = "Voiture non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable UUID id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    @Operation(summary = "Lister toutes les voitures", description = "Retourne la liste complète des voitures enregistrées")
    @ApiResponse(responseCode = "200", description = "Liste des voitures récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        List<Car> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @Operation(summary = "Lister les voitures à inspecter", description = "Retourne les voitures nécessitant une inspection technique")
    @ApiResponse(responseCode = "200", description = "Liste des voitures à inspecter")
    @GetMapping("/inspection")
    public ResponseEntity<List<Car>> getCarsToInspect() {
        List<Car> carsToInspect = carService.getCarsToInspect();
        return ResponseEntity.ok(carsToInspect);
    }
}