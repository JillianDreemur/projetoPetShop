# Pet Shop — Microsserviços

Projeto com Eureka, Gateway, microsserviços de **Pets** e **Agendamentos**, frontend **Thymeleaf** (`petshop-web`) e **PostgreSQL local**.

**Não usa Docker.** Só precisa de Java, Maven e PostgreSQL instalados na máquina.

## Pré-requisitos

- Java 17+
- Maven
- PostgreSQL instalado localmente (porta **5432**)

### Credenciais PostgreSQL (configuradas no projeto)

| Campo    | Valor      |
|----------|------------|
| Usuário  | `postgres` |
| Senha    | `5555`     |
| Banco    | `petshop`  |
| Porta    | `5432`     |

## 1. Banco de dados (automático no backend)

Crie **apenas o banco** `petshop` uma vez (pgAdmin ou psql):

```sql
CREATE DATABASE petshop;
```

As tabelas `pets` e `agendamentos` são criadas/atualizadas automaticamente pelo Hibernate (`ddl-auto: update`) quando você sobe os microsserviços.

## Eureka e Gateway — o que fazem?

**Eureka (porta 8761)** — “lista telefônica” dos serviços. Cada microsserviço se registra ao subir. O Gateway descobre onde cada um está.

**Gateway (porta 8080)** — porta única da API. O front fala com `localhost:8080` e o Gateway encaminha:
- `/pets/**` → microsserviço de Pets
- `/agendamentos/**` → microsserviço de Agendamentos

Fluxo: **Navegador (8091) → petshop-web → Gateway (8080) → microsserviço → PostgreSQL**.

## 2. Subir os serviços (ordem sugerida)

| Serviço              | Porta | Pasta                          |
|----------------------|-------|--------------------------------|
| Eureka               | 8761  | `eureka-server`                |
| service-pets         | 8081  | `ms-service-pets-main`         |
| service-agendamentos | 8082  | `ms-service-agendamentos-main` |
| Gateway              | 8080  | `gateway`                      |
| Frontend Thymeleaf   | 8091  | `petshop-web`                  |

Em cada pasta:

```powershell
mvn spring-boot:run
```

## 3. Navegação (tudo via Thymeleaf — use os botões, não precisa digitar URL)

### Site público (porta 8091)

| Botão no menu | Rota |
|---------------|------|
| Início | `/` ou `/inicio` |
| Pets | `/pets` |
| Agendamentos | `/agendamentos` |
| Área Admin | `/login` |

### Área administrativa (após login)

| Botão no menu | Rota |
|---------------|------|
| Visão geral | `/admin` |
| Serviços | `/admin/servicos` |
| Agendamentos | `/admin/agendamentos` |
| Site público | `/` |
| Sair | botão no menu |

**Primeiro acesso:** `/registro` → nome, senha e chave **`5555`** → depois `/login`.

Microsserviços (para subir): Eureka `8761`, Gateway `8080`, Pets `8081`, Agendamentos `8082`.

## Estrutura

```
petshop-web/                   Frontend Thymeleaf
eureka-server/                 Descoberta de serviços
gateway/                       API gateway
ms-service-pets-main/          Microsserviço de pets
ms-service-agendamentos-main/  Microsserviço de agendamentos
```

## API via Gateway

- `GET/POST/PUT/DELETE http://localhost:8080/pets`
- `GET/POST/PUT/DELETE http://localhost:8080/agendamentos`
