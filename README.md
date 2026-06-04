# Pet Shop — Microsserviços

Projeto com Eureka, Gateway, microsserviços de **Pets** e **Agendamentos**, frontend **Thymeleaf** (`petshop-web`) e **PostgreSQL local** (sem Docker para o banco).

## Pré-requisitos

- Java 17+
- Maven
- PostgreSQL instalado localmente (porta **5432**)
- RabbitMQ (local ou `docker compose up -d` só para o RabbitMQ)

### Credenciais PostgreSQL (configuradas no projeto)

| Campo    | Valor    |
|----------|----------|
| Usuário  | `postgre` |
| Senha    | `5555`   |
| Banco    | `petshop` |
| Porta    | `5432`   |

> Se a conexão falhar, confira no pgAdmin/psql se o usuário não é `postgres` (padrão do instalador). Nesse caso, altere `username` nos arquivos `application.yml` dos microsserviços.

## 1. Banco de dados (automático no backend)

Crie **apenas o banco** `petshop` uma vez (pgAdmin ou psql):

```sql
CREATE DATABASE petshop;
```

As tabelas `pets` e `agendamentos` são criadas/atualizadas automaticamente pelo Hibernate (`ddl-auto: update`) quando você sobe os microsserviços.

Scripts manuais opcionais em `database/` (não são obrigatórios).

## Eureka e Gateway — o que fazem?

**Eureka (porta 8761)** — “lista telefônica” dos serviços. Cada microsserviço (Pets, Agendamentos) se registra ao subir. Assim o Gateway descobre *onde* cada um está, sem você configurar IP/porta fixa em todo lugar.

**Gateway (porta 8080)** — porta única da API. O front e ferramentas externas falam só com `localhost:8080`. O Gateway encaminha:
- `/pets/**` → microsserviço de Pets
- `/agendamentos/**` → microsserviço de Agendamentos

Fluxo do painel: **Navegador (8090) → petshop-web → Gateway (8080) → microsserviço → PostgreSQL**.

## 2. RabbitMQ (opcional via Docker)

```powershell
docker compose up -d
```

Isso sobe apenas o RabbitMQ. O PostgreSQL **não** usa mais Docker neste projeto.

## 3. Subir os serviços (ordem sugerida)

| Serviço              | Porta | Pasta                          |
|----------------------|-------|--------------------------------|
| Eureka               | 8761  | `eureka-server`                |
| service-pets         | 8081  | `ms-service-pets-main`         |
| service-agendamentos | 8082  | `ms-service-agendamentos-main` |
| Gateway              | 8080  | `gateway`                      |
| **Frontend Thymeleaf** | **8090** | `petshop-web`              |

Em cada pasta:

```powershell
mvn spring-boot:run
```

## 4. Acessar o painel

Abra no navegador: **http://localhost:8090**

A interface chama o gateway em `http://localhost:8080` (pets e agendamentos).

## Estrutura

```
database/          Scripts SQL (PostgreSQL local)
petshop-web/       Frontend Thymeleaf (substitui o React em frontend/)
eureka-server/
gateway/
ms-service-pets-main/
ms-service-agendamentos-main/
frontend/          React antigo (não é mais necessário para rodar o projeto)
```

## API via Gateway

- `GET/POST/PUT/DELETE http://localhost:8080/pets`
- `GET/POST/PUT/DELETE http://localhost:8080/agendamentos`
