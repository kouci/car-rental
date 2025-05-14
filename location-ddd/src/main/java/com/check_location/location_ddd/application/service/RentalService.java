package com.check_location.location_ddd.application.service;


import com.check_location.location_ddd.domain.enums.CarStatus;
import com.check_location.location_ddd.domain.model.Car;
import com.check_location.location_ddd.domain.model.Client;
import com.check_location.location_ddd.domain.model.Rental;
import com.check_location.location_ddd.port.out.CarRepository;
import com.check_location.location_ddd.port.out.ClientRepository;
import com.check_location.location_ddd.port.out.RentalRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final ClientRepository clientRepository;
    private final CarRepository carRepository;

    public RentalService(RentalRepository rentalRepository, ClientRepository clientRepository, CarRepository carRepository) {
        this.rentalRepository = rentalRepository;
        this.clientRepository = clientRepository;
        this.carRepository = carRepository;
    }

    public Rental rentCar(Rental rental) {
        Client client = clientRepository.findClientById(rental.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        validateRentalRequest(client, car, rental);

        int rentalDuration = (int) ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate());
        car.setConsecutiveRentalDays(car.getConsecutiveRentalDays() + rentalDuration);
        carRepository.save(car);

        return rentalRepository.save(rental);
    }

    public Rental returnCar(UUID rentalId, LocalDate actualReturnDate) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        if (rental.getActualReturnDate() != null) {
            throw new RuntimeException("Car has already been returned");
        }

        long daysLate = ChronoUnit.DAYS.between(rental.getEndDate(), actualReturnDate);
        if (daysLate > 3) {
            rental.setPenaltyApplied(true);
        }

        rental.setActualReturnDate(actualReturnDate);
        rental = rentalRepository.save(rental);

        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        int rentalDuration = (int) ChronoUnit.DAYS.between(rental.getStartDate(), rental.getEndDate());
        car.setConsecutiveRentalDays(car.getConsecutiveRentalDays() - rentalDuration);
        car.setStatus(CarStatus.AVAILABLE);
        car.setOutOfService(false);

        carRepository.save(car);

        return rental;
    }

    private void validateRentalRequest(Client client, Car car, Rental rental) {
        if (client.isHasUnpaidDebt() || client.isHasBlockedDeposit()) {
            throw new RuntimeException("Client has unpaid debt or blocked deposit");
        }

        List<Rental> clientRentals = rentalRepository.findByClientId(client.getId());
        long activeRentals = clientRentals.stream().filter(Rental::isActive).count();
        if (activeRentals >= 2) {
            throw new RuntimeException("Client already has 2 active rentals");
        }

        Optional<Rental> carRental = rentalRepository.findByCarId(car.getId());
        boolean alreadyRented = carRental.stream().anyMatch(Rental::isActive);
        if (alreadyRented) {
            throw new RuntimeException("Car is already rented");
        }

        if (car.isOutOfService()) {
            throw new RuntimeException("Car is out of service");
        }

        if (car.getMileage() - car.getLastInspectionMileage() > 150_000) {
            throw new RuntimeException("Car requires technical inspection due to mileage");
        }

        if (car.getConsecutiveRentalDays() > 60) {
            throw new RuntimeException("Car cannot be rented more than 60 consecutive days without inspection");
        }

        if (car.getLastInspectionDate().isBefore(LocalDate.now().minusDays(90))) {
            throw new RuntimeException("Car requires technical inspection (90-day rule)");
        }

        long sameModelRental = clientRentals.stream()
                .filter(r -> r.getStartDate().isAfter(LocalDate.now().minusDays(15)))
                .filter(r -> r.getActualReturnDate() == null)
                .filter(r -> {
                    Car rentedCar = carRepository.findById(r.getCarId()).orElse(null);
                    return rentedCar != null && rentedCar.getModel().equalsIgnoreCase(car.getModel());
                })
                .count();

        if (sameModelRental > 0) {
            throw new RuntimeException("Client already rented a similar vehicle in the last 15 days");
        }

        int duration = rental.getEndDate().getDayOfYear() - rental.getStartDate().getDayOfYear();
        if (duration < 1 || duration > 30) {
            throw new RuntimeException("Rental duration must be between 1 and 30 days");
        }

        long totalCars = carRepository.findAll().size();
        long outOfServiceCars = carRepository.findAll().stream().filter(Car::isOutOfService).count();
        if (totalCars > 0 && ((double) outOfServiceCars / totalCars) > 0.3) {
            throw new RuntimeException("Cannot rent: more than 30% of fleet is out of service");
        }
    }
}