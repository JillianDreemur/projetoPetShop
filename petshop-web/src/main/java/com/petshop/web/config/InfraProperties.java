package com.petshop.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "petshop.infra")
public class InfraProperties {

    private String eurekaUrl = "http://localhost:8761/test";
    private String gatewayUrl = "http://localhost:8080/test";
    private String petsUrl = "http://localhost:8081/";
    private String agendamentosUrl = "http://localhost:8082/";

    public String getEurekaUrl() {
        return eurekaUrl;
    }

    public void setEurekaUrl(String eurekaUrl) {
        this.eurekaUrl = eurekaUrl;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getPetsUrl() {
        return petsUrl;
    }

    public void setPetsUrl(String petsUrl) {
        this.petsUrl = petsUrl;
    }

    public String getAgendamentosUrl() {
        return agendamentosUrl;
    }

    public void setAgendamentosUrl(String agendamentosUrl) {
        this.agendamentosUrl = agendamentosUrl;
    }
}
