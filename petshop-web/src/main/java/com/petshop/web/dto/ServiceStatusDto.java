package com.petshop.web.dto;

public class ServiceStatusDto {

    private final String nome;
    private final String url;
    private final String urlAbrir;
    private final int porta;
    private final boolean online;
    private final String detalhe;

    public ServiceStatusDto(String nome, String url, int porta, boolean online, String detalhe) {
        this(nome, url, montarUrlAbrir(url, porta), porta, online, detalhe);
    }

    public ServiceStatusDto(String nome, String url, String urlAbrir, int porta, boolean online, String detalhe) {
        this.nome = nome;
        this.url = url;
        this.urlAbrir = urlAbrir;
        this.porta = porta;
        this.online = online;
        this.detalhe = detalhe;
    }

    private static String montarUrlAbrir(String url, int porta) {
        try {
            java.net.URI uri = java.net.URI.create(url);
            String scheme = uri.getScheme() != null ? uri.getScheme() : "http";
            String host = uri.getHost() != null ? uri.getHost() : "localhost";
            return scheme + "://" + host + ":" + porta + "/";
        } catch (Exception ex) {
            return "http://localhost:" + porta + "/";
        }
    }

    public String getNome() {
        return nome;
    }

    public String getUrl() {
        return url;
    }

    public String getUrlAbrir() {
        return urlAbrir;
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
