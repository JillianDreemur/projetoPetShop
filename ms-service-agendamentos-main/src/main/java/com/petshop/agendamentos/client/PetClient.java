package com.petshop.agendamentos.client;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "SERVICE-PETS")
public interface PetClient {

    @GetMapping("/pets/{id}")
    PetResponse buscarPorId(@PathVariable("id") UUID id);

    @PostMapping("/pets/{id}/visitas")
    void incrementarVisitas(@PathVariable("id") UUID id);

    record PetResponse(UUID id, String nome, String raca, String nomeDono, Integer quantidadeVisitas) {
    }
}
