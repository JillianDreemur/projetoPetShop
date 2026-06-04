package com.petshop.pets.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PetsSchemaMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PetsSchemaMigration.class);

    private final JdbcTemplate jdbcTemplate;

    public PetsSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute("ALTER TABLE pets ADD COLUMN IF NOT EXISTS peso_kg DOUBLE PRECISION");
            jdbcTemplate.update("UPDATE pets SET peso_kg = 1.0 WHERE peso_kg IS NULL");
            jdbcTemplate.execute("ALTER TABLE pets DROP COLUMN IF EXISTS quantidade_visitas");
            log.info("Schema da tabela pets verificado (coluna peso_kg).");
        } catch (Exception ex) {
            log.warn("Não foi possível ajustar schema de pets automaticamente: {}", ex.getMessage());
        }
    }
}
