package com.petshop.web.service;

import com.petshop.web.dto.AgendamentoDto;
import com.petshop.web.dto.AgendamentoForm;
import com.petshop.web.entity.ServicoOferecido;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AgendamentoMontador {

    private final ServicoCatalogoService catalogo;

    public AgendamentoMontador(ServicoCatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    public AgendamentoDto montarParaApi(AgendamentoForm form) {
        List<ServicoOferecido> selecionados = catalogo.buscarPorIds(form.getServicosSelecionados());
        catalogo.validarCombinacao(selecionados);
        AgendamentoDto dto = new AgendamentoDto();
        dto.setData(LocalDate.parse(form.getData()).atStartOfDay());
        dto.setTipoServico(catalogo.montarDescricaoServicos(selecionados));
        dto.setValorTotal(catalogo.calcularTotal(selecionados).doubleValue());
        dto.setPetId(form.getPetId());
        return dto;
    }

    public void preencherServicosNoForm(AgendamentoForm form, String tipoServico) {
        if (tipoServico == null || tipoServico.isBlank()) {
            return;
        }
        List<String> nomes = Arrays.stream(tipoServico.split(" \\+ "))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        List<UUID> ids = new ArrayList<>();
        for (ServicoOferecido s : catalogo.listarEntidades()) {
            if (nomes.contains(s.getNome())) {
                ids.add(s.getId());
            }
        }
        form.setServicosSelecionados(ids);
    }
}
