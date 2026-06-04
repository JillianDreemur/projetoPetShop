package com.petshop.web.dto;

public class ServiceStatusDto {

    private final String nome;
    private final String url;
    private final int porta;
    private final boolean online;
    private final String detalhe;

    public ServiceStatusDto(String nome, String url, int porta, boolean online, String detalhe) {
        this.nome = nome;
        this.url = url;
        this.porta = porta;
        this.online = online;
        this.detalhe = detalhe;
    }

    public String getNome() {
        return nome;
    }

    public String getUrl() {
        return url;
    }

    public int getPorta() {
        return porta;
    }

    public boolean isOnline() {
        return online;
    }

    public String getDetalhe() {
        return detalhe;
    }
}
