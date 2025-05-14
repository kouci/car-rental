package com.check_location.location_ddd.domain.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class Rental {
    private UUID id;
    private UUID clientId;
    private UUID carId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualReturnDate;
    private boolean penaltyApplied;

    public boolean isActive() {
        return actualReturnDate == null;
    }
}
