package com.petshop.web.dto;

import java.util.UUID;

public class ServicoForm {

    private UUID id;
    private String nome;
    private String valor;
    private String categoria;
    private String grupoExclusivo;
    private boolean pacote;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getGrupoExclusivo() {
        return grupoExclusivo;
    }

    public void setGrupoExclusivo(String grupoExclusivo) {
        this.grupoExclusivo = grupoExclusivo;
    }

    public boolean isPacote() {
        return pacote;
    }

    public void setPacote(boolean pacote) {
        this.pacote = pacote;
    }
}
