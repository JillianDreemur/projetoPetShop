package com.petshop.agendamentos.repository;

import com.petshop.agendamentos.entity.Agendamento;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgendamentoRepository extends JpaRepository<Agendamento, UUID> {
}
