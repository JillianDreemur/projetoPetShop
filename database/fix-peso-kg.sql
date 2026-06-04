-- Rode no banco petshop se aparecer erro "coluna peso_kg não existe"
-- Depois reinicie o microsserviço ms-service-pets-main

ALTER TABLE pets ADD COLUMN IF NOT EXISTS peso_kg DOUBLE PRECISION;

UPDATE pets SET peso_kg = 1.0 WHERE peso_kg IS NULL;

ALTER TABLE pets ALTER COLUMN peso_kg SET NOT NULL;

-- Opcional: remover coluna antiga
-- ALTER TABLE pets DROP COLUMN IF EXISTS quantidade_visitas;
