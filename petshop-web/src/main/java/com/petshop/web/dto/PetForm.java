package com.petshop.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class PetForm {

    private UUID id;

    @NotBlank(message = "Informe o nome do pet")
    private String nome;

    @NotBlank(message = "Informe a raça")
    private String raca;

    @NotBlank(message = "Informe o nome do dono")
    private String nomeDono;

    @Min(value = 0, message = "Visitas não pode ser negativo")
    private Integer quantidadeVisitas = 0;

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
