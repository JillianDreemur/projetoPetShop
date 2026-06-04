package com.petshop.pets.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Nome do pet: use apenas letras e espaços")
    @Column(nullable = false)
    private String nome;

    @NotBlank
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Raça: use apenas letras e espaços")
    @Column(nullable = false)
    private String raca;

    @NotBlank
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Tutor: use apenas letras e espaços")
    @Column(nullable = false)
    private String nomeDono;

    @NotNull
    @Positive
    @Column(name = "peso_kg", nullable = false)
    private Double pesoKg;

    public UUID getId() {
        return id;
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
