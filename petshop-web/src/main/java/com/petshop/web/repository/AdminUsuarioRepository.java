package com.petshop.web.repository;

import com.petshop.web.entity.AdminUsuario;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUsuarioRepository extends JpaRepository<AdminUsuario, UUID> {

    Optional<AdminUsuario> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);
}
