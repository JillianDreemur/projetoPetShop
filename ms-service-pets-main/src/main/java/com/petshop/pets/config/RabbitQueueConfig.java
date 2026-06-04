package main.java.com.petshop.pets.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitQueueConfig {

    @Bean
    public Queue agendamentoConcluidoQueue() {
        return new Queue("agendamento.concluido", true);
    }
}
