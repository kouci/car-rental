package com.check.location.service;

import com.check.location.enums.CarStatus;
import com.check.location.model.Car;
import com.check.location.model.Rental;
import com.check.location.repository.CarRepository;
import com.check.location.repository.RentalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class BatchService {

    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;

    public BatchService(RentalRepository rentalRepository, CarRepository carRepository) {
        this.rentalRepository = rentalRepository;
        this.carRepository = carRepository;
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void executeBatch() {
        log.info("Batch execution started at {}", LocalDate.now());
        checkCarsForInspection();
        checkRentalsForLateReturns();
        logAndProcessCarsWithMissedInspections();
        log.info("Batch execution completed.");
    }

    private void checkCarsForInspection() {
        List<Car> carsToInspect = carRepository.findAll().stream()
                .filter(this::shouldInspectCar)
                .toList();

        carsToInspect.forEach(car ->
                log.info("Car needs inspection: ID={}, LastInspectionDate={}",
                        car.getId(), car.getLastInspectionDate()));
    }

    private boolean shouldInspectCar(Car car) {
        return car.getLastInspectionDate().plusDays(90).isBefore(LocalDate.now().plusDays(7));
    }

    private void checkRentalsForLateReturns() {
        List<Rental> lateRentals = rentalRepository.findAll().stream()
                .filter(rental -> ChronoUnit.DAYS.between(rental.getEndDate(), LocalDate.now()) > 3)
                .toList();

        lateRentals.forEach(rental ->
                log.info("Late rental detected: ID={}, EndDate={}",
                        rental.getId(), rental.getEndDate()));
    }

    private void logAndProcessCarsWithMissedInspections() {
        List<Car> carsWithMissedInspections = carRepository.findAll().stream()
                .filter(car -> car.getMissedInspections() >= 3)
                .toList();

        carsWithMissedInspections.forEach(car -> {
            log.info("Car declassified due to missed inspections: ID={}, MissedInspections={}",
                    car.getId(), car.getMissedInspections());
            car.setStatus(CarStatus.MAINTENANCE);
            carRepository.save(car);
        });
    }
}
