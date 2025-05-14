package com.check.location.service;

import com.check.location.model.Client;
import com.check.location.repository.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }


    public Client createClient(Client client) {
        if (client.getId() != null) {
            throw new IllegalArgumentException("New client cannot have an ID");
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
