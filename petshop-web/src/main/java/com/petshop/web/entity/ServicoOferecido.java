package com.petshop.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "servicos_oferecidos")
public class ServicoOferecido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 30)
    private String categoria;

    /** Grupo para regra banho/tosa ou remédios; vazio para serviços avulsos (ex.: vacina). */
    @Column(name = "grupo_exclusivo", length = 30)
    private String grupoExclusivo;

    /** Pacote combo (banho e tosa, 2 em 1) — não combina com itens do mesmo grupo. */
    @Column(nullable = false)
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
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
