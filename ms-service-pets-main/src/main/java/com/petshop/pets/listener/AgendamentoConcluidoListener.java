package com.petshop.pets.listener;

import com.petshop.pets.service.PetService;
import java.util.UUID;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AgendamentoConcluidoListener {

    private final PetService petService;

    public AgendamentoConcluidoListener(PetService petService) {
        this.petService = petService;
    }

    @RabbitListener(queues = "${rabbitmq.queue.agendamento-concluido}")
    public void consumirMensagem(String petId) {
        petService.incrementarVisitas(UUID.fromString(petId));
    }
}
