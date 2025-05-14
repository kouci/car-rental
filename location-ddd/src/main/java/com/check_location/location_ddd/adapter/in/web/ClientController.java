package com.check_location.location_ddd.adapter.in.web;


import com.check_location.location_ddd.application.service.ClientService;
import com.check_location.location_ddd.domain.model.Client;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Client API", description = "Opérations liées aux clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Créer un nouveau client", description = "Crée un client et le sauvegarde dans le système")
    @ApiResponse(responseCode = "200", description = "Client créé avec succès")
    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        Client createdClient = clientService.createClient(client);
        return ResponseEntity.ok(createdClient);
    }

    @Operation(summary = "Récupérer un client par ID", description = "Retourne un client en fonction de son UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable UUID id) {
        Client client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    @Operation(summary = "Lister tous les clients", description = "Retourne la liste complète des clients enregistrés")
    @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès")
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }
}