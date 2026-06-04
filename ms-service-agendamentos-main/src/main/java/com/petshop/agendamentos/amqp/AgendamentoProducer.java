package com.petshop.agendamentos.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AgendamentoProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.agendamento-concluido}")
    private String queue;

    public AgendamentoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarPetConcluido(String petId) {
        rabbitTemplate.convertAndSend(queue, petId);
    }
}
