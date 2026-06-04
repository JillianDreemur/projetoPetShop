package com.petshop.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AgendamentoForm {

    private UUID id;

    @NotBlank(message = "Informe a data do agendamento")
    private String data;

    private List<UUID> servicosSelecionados = new ArrayList<>();

    private String tipoServico;

    @NotNull(message = "Informe o pet")
    private UUID petId;

    private String formaPagamento;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<UUID> getServicosSelecionados() {
        return servicosSelecionados;
    }

    public void setServicosSelecionados(List<UUID> servicosSelecionados) {
        this.servicosSelecionados = servicosSelecionados != null ? servicosSelecionados : new ArrayList<>();
    }

    public String getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(String tipoServico) {
        this.tipoServico = tipoServico;
    }

    public UUID getPetId() {
        return petId;
    }

    public void setPetId(UUID petId) {
        this.petId = petId;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
}
