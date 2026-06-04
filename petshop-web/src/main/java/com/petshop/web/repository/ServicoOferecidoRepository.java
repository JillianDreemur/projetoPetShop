package com.petshop.web.repository;

import com.petshop.web.entity.ServicoOferecido;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoOferecidoRepository extends JpaRepository<ServicoOferecido, UUID> {

    List<ServicoOferecido> findAllByOrderByCategoriaAscNomeAsc();

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, UUID id);
}
