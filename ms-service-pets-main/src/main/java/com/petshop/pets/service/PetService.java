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
        validarPeso(pet.getPesoKg());
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
        validarPeso(dados.getPesoKg());
        Pet pet = buscarPorId(id);
        pet.setNome(dados.getNome());
        pet.setRaca(dados.getRaca());
        pet.setNomeDono(dados.getNomeDono());
        pet.setPesoKg(dados.getPesoKg());
        return petRepository.save(pet);
    }

    public void deletar(UUID id) {
        if (!petRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet não encontrado");
        }
        petRepository.deleteById(id);
    }

    private void validarPeso(Double pesoKg) {
        if (pesoKg == null || pesoKg <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Peso deve ser maior que zero (kg)");
        }
    }
}
