package com.petshop.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ServicoOferecidoDto {

    private final UUID id;
    private final String nome;
    private final String preco;
    private final BigDecimal valorNumerico;
    private final String categoria;
    private final String grupoExclusivo;
    private final boolean pacote;

    public ServicoOferecidoDto(UUID id, String nome, String preco, BigDecimal valorNumerico,
                               String categoria, String grupoExclusivo, boolean pacote) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.valorNumerico = valorNumerico;
        this.categoria = categoria;
        this.grupoExclusivo = grupoExclusivo;
        this.pacote = pacote;
    }

    public UUID getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getPreco() {
        return preco;
    }

    public BigDecimal getValorNumerico() {
        return valorNumerico;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getGrupoExclusivo() {
        return grupoExclusivo;
    }

    public boolean isPacote() {
        return pacote;
    }
}
