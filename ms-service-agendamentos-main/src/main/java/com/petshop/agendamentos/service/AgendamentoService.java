package com.petshop.agendamentos.service;

import com.petshop.agendamentos.amqp.AgendamentoProducer;
import com.petshop.agendamentos.client.PetClient;
import com.petshop.agendamentos.entity.Agendamento;
import com.petshop.agendamentos.repository.AgendamentoRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PetClient petClient;
    private final AgendamentoProducer agendamentoProducer;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              PetClient petClient,
                              AgendamentoProducer agendamentoProducer) {
        this.agendamentoRepository = agendamentoRepository;
        this.petClient = petClient;
        this.agendamentoProducer = agendamentoProducer;
    }

    public Agendamento salvar(Agendamento agendamento) {
        try {
            petClient.buscarPorId(agendamento.getPetId());
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet informado não existe");
        }

        Agendamento salvo = agendamentoRepository.save(agendamento);
        agendamentoProducer.publicarPetConcluido(salvo.getPetId().toString());
        return salvo;
    }

    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public Agendamento buscarPorId(UUID id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));
    }

    public Agendamento atualizar(UUID id, Agendamento dados) {
        Agendamento agendamento = buscarPorId(id);
        agendamento.setData(dados.getData());
        agendamento.setTipoServico(dados.getTipoServico());
        agendamento.setPetId(dados.getPetId());
        return agendamentoRepository.save(agendamento);
    }

    public void deletar(UUID id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Agendamento não encontrado");
        }
        agendamentoRepository.deleteById(id);
    }
}
