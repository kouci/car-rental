package com.check_location.location_ddd.adapter.out.persistence;


import com.check_location.location_ddd.domain.model.Client;
import com.check_location.location_ddd.port.out.ClientRepository;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class ClientRepositoryImpl implements ClientRepository {

    private final Map<UUID, Client> clients = new ConcurrentHashMap<>();

    @Override
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

    @Override
    public Optional<Client> findClientById(UUID clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID cannot be null");
        }
        return Optional.ofNullable(clients.get(clientId));
    }

    @Override
    public List<Client> findAllClients() {
        return new ArrayList<>(clients.values());
    }
}