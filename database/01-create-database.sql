-- Rode este arquivo PRIMEIRO, conectado ao banco "postgres" (banco padrão).
-- Ex.: psql -U postgres -d postgres -f database/01-create-database.sql

CREATE DATABASE petshop
    WITH ENCODING 'UTF8'
    TEMPLATE template0;
