package com.petshop.pets.service;

import com.petshop.pets.entity.Pet;
import com.petshop.pets.repository.PetRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public Pet salvar(Pet pet) {
        return petRepository.save(pet);
    }

    public List<Pet> listarTodos() {
        return petRepository.findAll();
    }

    public Pet buscarPorId(UUID id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado"));
    }

    public Pet atualizar(UUID id, Pet dados) {
        Pet pet = buscarPorId(id);
        pet.setNome(dados.getNome());
        pet.setRaca(dados.getRaca());
        pet.setNomeDono(dados.getNomeDono());
        pet.setQuantidadeVisitas(dados.getQuantidadeVisitas());
        return petRepository.save(pet);
    }

    public void deletar(UUID id) {
        if (!petRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado");
        }
        petRepository.deleteById(id);
    }

    public void incrementarVisitas(UUID petId) {
        Pet pet = buscarPorId(petId);
        pet.setQuantidadeVisitas((pet.getQuantidadeVisitas() == null ? 0 : pet.getQuantidadeVisitas()) + 1);
        petRepository.save(pet);
    }
}
