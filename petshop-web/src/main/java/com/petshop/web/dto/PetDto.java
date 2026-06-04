package com.petshop.web.dto;

import java.util.UUID;

public class PetDto {

    private UUID id;
    private String nome;
    private String raca;
    private String nomeDono;
    private Integer quantidadeVisitas;

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

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public String getNomeDono() {
        return nomeDono;
    }

    public void setNomeDono(String nomeDono) {
        this.nomeDono = nomeDono;
    }

    public Integer getQuantidadeVisitas() {
        return quantidadeVisitas;
    }

    public void setQuantidadeVisitas(Integer quantidadeVisitas) {
        this.quantidadeVisitas = quantidadeVisitas;
    }
}
