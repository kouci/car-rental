package com.check.location.service;

import com.check.location.model.Client;
import com.check.location.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    private ClientRepository clientRepository;
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientRepository = mock(ClientRepository.class);
        clientService = new ClientService(clientRepository);
    }

    @Test
    void shouldCreateClientSuccessfully() {
        Client newClient = new Client();
        newClient.setId(null);
        newClient.setName("Jean Dupont");
        newClient.setEmail("jean.dupont@example.com");
        newClient.setHasUnpaidDebt(false);
        newClient.setHasBlockedDeposit(false);

        Client savedClient = new Client();
        savedClient.setId(UUID.randomUUID());
        savedClient.setName("Jean Dupont");
        savedClient.setEmail("jean.dupont@example.com");
        savedClient.setHasUnpaidDebt(false);
        savedClient.setHasBlockedDeposit(false);

        when(clientRepository.save(newClient)).thenReturn(savedClient);

        Client result = clientService.createClient(newClient);

        assertNotNull(result);
        assertEquals("Jean Dupont", result.getName());
        verify(clientRepository, times(1)).save(newClient);
    }

    @Test
    void shouldThrowExceptionWhenCreatingClientWithId() {
        Client clientWithId = new Client();
        clientWithId.setId(UUID.randomUUID());
        clientWithId.setName("Déjà existant");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            clientService.createClient(clientWithId);
        });

        assertEquals("New client cannot have an ID", exception.getMessage());
    }

    @Test
    void shouldReturnClientById() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setName("Sophie Martin");
        client.setEmail("sophie.martin@example.com");
        client.setHasUnpaidDebt(false);
        client.setHasBlockedDeposit(true);

        when(clientRepository.findClientById(clientId)).thenReturn(Optional.of(client));

        Client result = clientService.getClientById(clientId);

        assertNotNull(result);
        assertEquals("Sophie Martin", result.getName());
        assertEquals("sophie.martin@example.com", result.getEmail());
        assertTrue(result.isHasBlockedDeposit());
        verify(clientRepository).findClientById(clientId);
    }

    @Test
    void shouldThrowExceptionIfClientNotFoundById() {
        UUID clientId = UUID.randomUUID();

        when(clientRepository.findClientById(clientId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            clientService.getClientById(clientId);
        });

        assertEquals("Client not found with ID: " + clientId, exception.getMessage());
    }

    @Test
    void shouldReturnAllClients() {
        List<Client> clients = List.of(
                new Client(UUID.randomUUID(), "Alice", "alice@example.com", false, false),
                new Client(UUID.randomUUID(), "Bob", "bob@example.com", false, true)
        );

        when(clientRepository.findAllClients()).thenReturn(clients);

        List<Client> result = clientService.getAllClients();

        assertEquals(2, result.size());
        verify(clientRepository, times(1)).findAllClients();
    }
}
