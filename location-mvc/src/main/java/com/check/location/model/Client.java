package com.check.location.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Client {
    private UUID id;
    private String name;
    private String email;
    private boolean hasUnpaidDebt;
    private boolean hasBlockedDeposit;
}
