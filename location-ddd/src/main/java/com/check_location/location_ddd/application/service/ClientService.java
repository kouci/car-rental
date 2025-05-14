package com.check_location.location_ddd.application.service;

import com.check_location.location_ddd.domain.model.Client;
import com.check_location.location_ddd.port.out.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client createClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client cannot be null");
        }
        if (client.getId() != null) {
            throw new IllegalArgumentException("New client must not have an ID");
        }
        return clientRepository.save(client);
    }

    public Client getClientById(UUID id) {
        return clientRepository.findClientById(id)
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAllClients();
    }
}
