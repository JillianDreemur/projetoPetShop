package com.petshop.web.service;

import com.petshop.web.dto.AgendamentoDto;
import com.petshop.web.entity.ServicoOferecido;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class VagaBanhoTosaService {

    public static final int LIMITE_DIARIO = 3;
    public static final String GRUPO_BANHO_TOSA = "banho_tosa";

    private static final DateTimeFormatter FMT_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ServicoCatalogoService catalogo;

    public VagaBanhoTosaService(ServicoCatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    public boolean selecaoIncluiBanhoTosa(List<ServicoOferecido> selecionados) {
        return selecionados.stream()
                .anyMatch(s -> GRUPO_BANHO_TOSA.equals(s.getGrupoExclusivo()));
    }

    public int contarOcupacao(LocalDate data, List<AgendamentoDto> agendamentos, UUID ignorarId) {
        Set<String> nomesEstetica = nomesBanhoTosa();
        return (int) agendamentos.stream()
                .filter(a -> a.getData() != null && a.getData().toLocalDate().equals(data))
                .filter(a -> ignorarId == null || !ignorarId.equals(a.getId()))
                .filter(a -> incluiBanhoTosa(a, nomesEstetica))
                .count();
    }

    public boolean diaCheio(LocalDate data, List<AgendamentoDto> agendamentos, UUID ignorarId) {
        return contarOcupacao(data, agendamentos, ignorarId) >= LIMITE_DIARIO;
    }

    public Optional<LocalDate> proximaDataComVaga(LocalDate aPartir, List<AgendamentoDto> agendamentos,
                                                  UUID ignorarId) {
        LocalDate dia = aPartir;
        for (int i = 0; i < 366; i++) {
            if (!diaCheio(dia, agendamentos, ignorarId)) {
                return Optional.of(dia);
            }
            dia = dia.plusDays(1);
        }
        return Optional.empty();
    }

    public String mensagemDiaCheio(LocalDate dataCheia, List<AgendamentoDto> agendamentos, UUID ignorarId) {
        Optional<LocalDate> proxima = proximaDataComVaga(dataCheia.plusDays(1), agendamentos, ignorarId);
        String base = "Este dia já está cheio para banho/tosa (máximo " + LIMITE_DIARIO + " por dia).";
        return proxima.map(d -> base + " Próxima data com vaga: " + d.format(FMT_BR) + ".")
                .orElse(base + " Tente outra data.");
    }

    public Map<String, Integer> mapaOcupacaoPorData(List<AgendamentoDto> agendamentos) {
        Set<String> nomesEstetica = nomesBanhoTosa();
        Map<String, Integer> mapa = new HashMap<>();
        for (AgendamentoDto ag : agendamentos) {
            if (!incluiBanhoTosa(ag, nomesEstetica) || ag.getData() == null) {
                continue;
            }
            String chave = ag.getData().toLocalDate().toString();
            mapa.merge(chave, 1, Integer::sum);
        }
        return mapa;
    }

    private Set<String> nomesBanhoTosa() {
        return catalogo.listarEntidades().stream()
                .filter(s -> GRUPO_BANHO_TOSA.equals(s.getGrupoExclusivo()))
                .map(ServicoOferecido::getNome)
                .collect(Collectors.toCollection(HashSet::new));
    }

    private boolean incluiBanhoTosa(AgendamentoDto ag, Set<String> nomesEstetica) {
        if (ag.getTipoServico() == null || ag.getTipoServico().isBlank()) {
            return false;
        }
        String[] partes = ag.getTipoServico().split(" \\+ ");
        for (String parte : partes) {
            if (nomesEstetica.contains(parte.trim())) {
                return true;
            }
        }
        return false;
    }
}
