package com.petshop.agendamentos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AgendamentosSchemaMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AgendamentosSchemaMigration.class);

    private final JdbcTemplate jdbcTemplate;

    public AgendamentosSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute("ALTER TABLE agendamentos ADD COLUMN IF NOT EXISTS valor_total DOUBLE PRECISION");
            log.info("Schema da tabela agendamentos verificado (coluna valor_total).");
        } catch (Exception ex) {
            log.warn("Não foi possível ajustar schema de agendamentos: {}", ex.getMessage());
        }
    }
}
