package com.check_location.location_ddd.port.out;

import com.check_location.location_ddd.domain.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Client save(Client client);
    Optional<Client> findClientById(UUID clientId);
    List<Client> findAllClients();
}