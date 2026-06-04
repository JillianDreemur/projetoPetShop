package com.petshop.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public class PetForm {

    private UUID id;

    @NotBlank(message = "Informe o nome do pet")
    private String nome;

    @NotBlank(message = "Informe a raça")
    private String raca;

    @NotBlank(message = "Informe o nome do tutor")
    private String nomeDono;

    @NotNull(message = "Informe o peso em kg")
    @Positive(message = "Peso deve ser maior que zero (kg)")
    private Double pesoKg;

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

    public Double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(Double pesoKg) {
        this.pesoKg = pesoKg;
    }
}
