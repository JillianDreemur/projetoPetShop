package com.petshop.web.dto;

public class ServicoOferecidoDto {

    private final String nome;
    private final String preco;
    private final String categoria;

    public ServicoOferecidoDto(String nome, String preco) {
        this(nome, preco, "geral");
    }

    public ServicoOferecidoDto(String nome, String preco, String categoria) {
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public String getPreco() {
        return preco;
    }

    public String getCategoria() {
        return categoria;
    }
}
