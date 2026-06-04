package com.petshop.web.service;

import com.petshop.web.dto.ServicoOferecidoDto;
import com.petshop.web.entity.ServicoOferecido;
import com.petshop.web.repository.ServicoOferecidoRepository;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ServicoCatalogoService {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final ServicoOferecidoRepository repository;

    public ServicoCatalogoService(ServicoOferecidoRepository repository) {
        this.repository = repository;
    }

    public List<ServicoOferecido> listarEntidades() {
        return repository.findAllByOrderByCategoriaAscNomeAsc();
    }

    public List<ServicoOferecidoDto> listarParaExibicao() {
        return listarEntidades().stream().map(this::paraDto).toList();
    }

    public ServicoOferecido buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado"));
    }

    public List<ServicoOferecido> buscarPorIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<ServicoOferecido> encontrados = new ArrayList<>();
        for (UUID id : ids) {
            encontrados.add(buscar(id));
        }
        return encontrados;
    }

    public void salvar(String nome, BigDecimal valor, String categoria, String grupoExclusivo,
                       boolean pacote, UUID idEdicao) {
        String nomeLimpo = nome != null ? nome.trim() : "";
        if (nomeLimpo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o nome do serviço");
        }
        if (valor == null || valor.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor deve ser maior que zero");
        }
        if (idEdicao == null) {
            if (repository.existsByNomeIgnoreCase(nomeLimpo)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um serviço com este nome");
            }
            ServicoOferecido novo = new ServicoOferecido();
            preencher(novo, nomeLimpo, valor, categoria, grupoExclusivo, pacote);
            repository.save(novo);
        } else {
            if (repository.existsByNomeIgnoreCaseAndIdNot(nomeLimpo, idEdicao)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um serviço com este nome");
            }
            ServicoOferecido existente = buscar(idEdicao);
            preencher(existente, nomeLimpo, valor, categoria, grupoExclusivo, pacote);
            repository.save(existente);
        }
    }

    public void excluir(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Serviço não encontrado");
        }
        repository.deleteById(id);
    }

    public void validarCombinacao(List<ServicoOferecido> selecionados) {
        if (selecionados == null || selecionados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione ao menos um serviço");
        }
        validarGrupo(selecionados, "banho_tosa",
                "Não é possível agendar Banho e tosa junto com Banho ou Tosa separados.");
        validarGrupo(selecionados, "remedio",
                "Não é possível agendar o pacote 2 em 1 junto com remédios individuais.");
    }

    public BigDecimal calcularTotal(List<ServicoOferecido> selecionados) {
        return selecionados.stream()
                .map(ServicoOferecido::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String formatarValor(BigDecimal valor) {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(PT_BR);
        return fmt.format(valor);
    }

    public String montarDescricaoServicos(List<ServicoOferecido> selecionados) {
        return selecionados.stream().map(ServicoOferecido::getNome).reduce((a, b) -> a + " + " + b).orElse("");
    }

    private void validarGrupo(List<ServicoOferecido> selecionados, String grupo, String mensagem) {
        List<ServicoOferecido> doGrupo = selecionados.stream()
                .filter(s -> grupo.equals(s.getGrupoExclusivo()))
                .toList();
        if (doGrupo.size() <= 1) {
            return;
        }
        boolean temPacote = doGrupo.stream().anyMatch(ServicoOferecido::isPacote);
        boolean temItem = doGrupo.stream().anyMatch(s -> !s.isPacote());
        if (temPacote && temItem) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
    }

    private void preencher(ServicoOferecido s, String nome, BigDecimal valor, String categoria,
                           String grupoExclusivo, boolean pacote) {
        s.setNome(nome);
        s.setValor(valor);
        s.setCategoria(categoria != null ? categoria : "geral");
        s.setGrupoExclusivo(grupoExclusivo != null && !grupoExclusivo.isBlank() ? grupoExclusivo : null);
        s.setPacote(pacote);
    }

    private ServicoOferecidoDto paraDto(ServicoOferecido s) {
        return new ServicoOferecidoDto(
                s.getId(),
                s.getNome(),
                formatarValor(s.getValor()),
                s.getValor(),
                s.getCategoria(),
                s.getGrupoExclusivo(),
                s.isPacote()
        );
    }
}
