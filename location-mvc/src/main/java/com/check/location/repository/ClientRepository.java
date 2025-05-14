package com.check.location.repository;


import com.check.location.model.Client;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ClientRepository {

    public final Map<UUID, Client> clients = new ConcurrentHashMap<>();

    public Client save(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (client.getId() == null) {
            client.setId(UUID.randomUUID());
        }
        clients.put(client.getId(), client);

        return client;
    }

    public Optional<Client> findClientById(UUID clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        return Optional.ofNullable(clients.get(clientId));
    }
    public List<Client> findAllClients() {
        return new ArrayList<>(clients.values());
    }

}
