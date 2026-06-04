package com.petshop.web.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.petshop.web.dto.AgendamentoDto;
import com.petshop.web.dto.AgendamentoForm;
import com.petshop.web.dto.PetDto;
import com.petshop.web.dto.PetForm;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class GatewayApiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public GatewayApiClient(RestClient gatewayRestClient, ObjectMapper objectMapper) {
        this.restClient = gatewayRestClient;
        this.objectMapper = objectMapper;
    }

    public List<PetDto> listarPets() {
        PetDto[] pets = restClient.get()
                .uri("/pets")
                .retrieve()
                .body(PetDto[].class);
        return pets == null ? Collections.emptyList() : Arrays.asList(pets);
    }

    public List<AgendamentoDto> listarAgendamentos() {
        AgendamentoDto[] agendamentos = restClient.get()
                .uri("/agendamentos")
                .retrieve()
                .body(AgendamentoDto[].class);
        return agendamentos == null ? Collections.emptyList() : Arrays.asList(agendamentos);
    }

    public PetDto buscarPet(UUID id) {
        return restClient.get()
                .uri("/pets/{id}", id)
                .retrieve()
                .body(PetDto.class);
    }

    public AgendamentoDto buscarAgendamento(UUID id) {
        return restClient.get()
                .uri("/agendamentos/{id}", id)
                .retrieve()
                .body(AgendamentoDto.class);
    }

    public void criarPet(PetDto pet) {
        restClient.post()
                .uri("/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .body(pet)
                .retrieve()
                .toBodilessEntity();
    }

    public void atualizarPet(UUID id, PetDto pet) {
        restClient.put()
                .uri("/pets/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(pet)
                .retrieve()
                .toBodilessEntity();
    }

    public void excluirPet(UUID id) {
        restClient.delete()
                .uri("/pets/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    public void criarAgendamento(AgendamentoDto agendamento) {
        restClient.post()
                .uri("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(agendamento)
                .retrieve()
                .toBodilessEntity();
    }

    public void atualizarAgendamento(UUID id, AgendamentoDto agendamento) {
        restClient.put()
                .uri("/agendamentos/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(agendamento)
                .retrieve()
                .toBodilessEntity();
    }

    public void excluirAgendamento(UUID id) {
        restClient.delete()
                .uri("/agendamentos/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    public String extrairMensagemErro(Exception ex) {
        if (ex instanceof RestClientResponseException responseEx) {
            try {
                JsonNode node = objectMapper.readTree(responseEx.getResponseBodyAsString());
                if (node.has("error") && !node.get("error").asText().isBlank()) {
                    return node.get("error").asText();
                }
                if (node.has("message") && !node.get("message").asText().isBlank()) {
                    return node.get("message").asText();
                }
            } catch (Exception ignored) {
                // usa mensagem padrão abaixo
            }
            HttpStatusCode status = responseEx.getStatusCode();
            return "Erro na API (" + status.value() + "): " + responseEx.getStatusText();
        }
        return ex.getMessage() != null ? ex.getMessage() : "Erro ao comunicar com o gateway.";
    }

    /** Mensagem curta para o cliente após falha em salvar (sem JDBC/SQL). */
    public String mensagemOperacaoCliente(Exception ex) {
        String msg = extrairMensagemErro(ex);
        if (msg == null || msg.isBlank()) {
            return "Não foi possível concluir. Verifique se os serviços estão ativos e tente de novo.";
        }
        String lower = msg.toLowerCase();
        if (lower.contains("jdbc") || lower.contains("sql") || lower.contains("hibernate")
                || lower.contains("eureka") || lower.contains("gateway") || lower.contains("coluna")) {
            return "Não foi possível salvar agora. Tente novamente em alguns minutos.";
        }
        if (msg.length() <= 120) {
            return msg;
        }
        return "Não foi possível concluir. Verifique os dados e tente de novo.";
    }

    /** Mensagem simples para o painel do cliente (sem detalhes técnicos). */
    public String mensagemParaCliente(Exception ex) {
        String tecnica = extrairMensagemErro(ex);
        if (tecnica == null || tecnica.isBlank()) {
            return "Não foi possível conectar ao sistema agora. Tente novamente em instantes.";
        }
        String lower = tecnica.toLowerCase();
        if (lower.contains("jdbc")
                || lower.contains("sql")
                || lower.contains("coluna")
                || lower.contains("erro:")
                || lower.contains("hibernate")
                || lower.contains("connection")
                || lower.contains("eureka")
                || lower.contains("gateway")
                || lower.contains("microsserv")) {
            return "Não foi possível carregar os dados agora. Tente novamente em alguns minutos.";
        }
        if (tecnica.length() > 120) {
            return "Não foi possível concluir a operação. Verifique os dados e tente de novo.";
        }
        return tecnica;
    }

    public PetDto toPetDto(PetForm form) {
        PetDto dto = new PetDto();
        dto.setNome(form.getNome());
        dto.setRaca(form.getRaca());
        dto.setNomeDono(form.getNomeDono());
        dto.setPesoKg(form.getPesoKg());
        return dto;
    }

    public AgendamentoDto toAgendamentoDtoAdmin(AgendamentoForm form) {
        AgendamentoDto dto = new AgendamentoDto();
        dto.setData(parseDataAgendamento(form.getData()));
        dto.setTipoServico(form.getTipoServico());
        dto.setPetId(form.getPetId());
        return dto;
    }

    private LocalDateTime parseDataAgendamento(String data) {
        if (data == null || data.isBlank()) {
            throw new IllegalArgumentException("Data inválida");
        }
        if (data.length() == 10) {
            return java.time.LocalDate.parse(data).atStartOfDay();
        }
        if (data.length() == 16) {
            return LocalDateTime.parse(data + ":00");
        }
        return LocalDateTime.parse(data);
    }
}
