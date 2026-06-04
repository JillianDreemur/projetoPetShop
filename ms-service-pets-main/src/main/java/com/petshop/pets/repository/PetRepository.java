package com.petshop.pets.repository;

import com.petshop.pets.entity.Pet;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, UUID> {
}
