package com.check_location.location_ddd.domain.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Client {
    private UUID id;
    private String name;
    private String email;
    private boolean hasUnpaidDebt;
    private boolean hasBlockedDeposit;
}
