package com.check_location.location_ddd.domain.model;

import com.check.location.enums.CarStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;


@Data
public class Car {

    private UUID id;
    private String brand;
    private String model;
    private int mileage;
    private CarStatus status;
    private LocalDate lastInspectionDate;
    private int lastInspectionMileage;
    private int consecutiveRentalDays;
    private int missedInspections;
    private boolean isOutOfService;
}
