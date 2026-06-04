package com.petshop.web.service;

import com.petshop.web.dto.ServicoOferecidoDto;
import java.util.List;

public final class CatalogoServicos {

    private CatalogoServicos() {
    }

    public static List<ServicoOferecidoDto> listarTodos() {
        return List.of(
                new ServicoOferecidoDto("Banho", "R$ 50,00", "estetica"),
                new ServicoOferecidoDto("Tosa", "R$ 30,00", "estetica"),
                new ServicoOferecidoDto("Banho e tosa", "R$ 70,00", "estetica"),
                new ServicoOferecidoDto("Vacina", "R$ 100,00", "vacina"),
                new ServicoOferecidoDto("Remédio para carrapato", "R$ 80,00", "remedio"),
                new ServicoOferecidoDto("Remédio para verme", "R$ 60,00", "remedio"),
                new ServicoOferecidoDto("Remédio carrapato e verme (2 em 1)", "R$ 100,00", "remedio")
        );
    }

    public static List<String> nomesParaAgendamento() {
        return listarTodos().stream().map(ServicoOferecidoDto::getNome).toList();
    }
}
