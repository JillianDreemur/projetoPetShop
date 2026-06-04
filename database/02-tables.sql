-- Rode DEPOIS do 01, já conectado ao banco petshop.
-- Ex.: psql -U postgres -d petshop -f database/02-tables.sql

CREATE TABLE IF NOT EXISTS pets (
    id                  UUID PRIMARY KEY,
    nome                VARCHAR(255) NOT NULL,
    raca                VARCHAR(255) NOT NULL,
    nome_dono           VARCHAR(255) NOT NULL,
    quantidade_visitas  INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS agendamentos (
    id            UUID PRIMARY KEY,
    data          TIMESTAMP NOT NULL,
    tipo_servico  VARCHAR(255) NOT NULL,
    pet_id        UUID NOT NULL,
    CONSTRAINT fk_agendamento_pet
        FOREIGN KEY (pet_id) REFERENCES pets (id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_agendamentos_pet_id ON agendamentos (pet_id);

INSERT INTO pets (id, nome, raca, nome_dono, quantidade_visitas) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Rex', 'Labrador', 'João Silva', 2),
    ('22222222-2222-2222-2222-222222222222', 'Mia', 'Persa', 'Ana Costa', 0)
ON CONFLICT (id) DO NOTHING;

INSERT INTO agendamentos (id, data, tipo_servico, pet_id) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '2026-06-10 14:00:00', 'Banho e tosa', '11111111-1111-1111-1111-111111111111')
ON CONFLICT (id) DO NOTHING;
