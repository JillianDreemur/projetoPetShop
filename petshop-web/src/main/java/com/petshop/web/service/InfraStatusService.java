package com.petshop.web.service;

import com.petshop.web.config.InfraProperties;
import com.petshop.web.dto.ServiceStatusDto;
import java.net.URI;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class InfraStatusService {

    private final RestClient restClient;
    private final InfraProperties infra;

    public InfraStatusService(InfraProperties infra) {
        this.infra = infra;
        this.restClient = RestClient.builder()
                .build();
    }

    public List<ServiceStatusDto> verificarTodos() {
        return List.of(
                verificar("Eureka", infra.getEurekaUrl()),
                verificar("Gateway", infra.getGatewayUrl()),
                verificar("Service Pets", infra.getPetsUrl()),
                verificar("Service Agendamentos", infra.getAgendamentosUrl()),
                new ServiceStatusDto("Petshop Web (front)", "http://localhost:8091/test",
                        "http://localhost:8091/", 8091, true, "Este painel")
        );
    }

    public long contarOnline(List<ServiceStatusDto> status) {
        return status.stream().filter(ServiceStatusDto::isOnline).count();
    }

    private ServiceStatusDto verificar(String nome, String url) {
        int porta = extrairPorta(url);
        try {
            restClient.get()
                    .uri(URI.create(url))
                    .retrieve()
                    .toBodilessEntity();
            return new ServiceStatusDto(nome, url, porta, true, "Respondendo normalmente");
        } catch (Exception ex) {
            String msg = ex.getMessage() != null ? ex.getMessage() : "Sem resposta";
            if (msg.length() > 80) {
                msg = msg.substring(0, 77) + "...";
            }
            return new ServiceStatusDto(nome, url, porta, false, msg);
        }
    }

    private int extrairPorta(String url) {
        try {
            URI uri = URI.create(url);
            if (uri.getPort() > 0) {
                return uri.getPort();
            }
            return "https".equals(uri.getScheme()) ? 443 : 80;
        } catch (Exception ex) {
            return 0;
        }
    }
}
