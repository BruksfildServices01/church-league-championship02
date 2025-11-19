🏆 README.md — Church League Championship (COM BADGES)

🔥 Pronto para colar no seu GitHub
🔥 Com badges
🔥 Com seções profissionais
🔥 Estético e organizado

# 🏆 Church League Championship  
Sistema de gerenciamento de campeonatos de futebol — Spring Boot + PostgreSQL + Docker

![Java](https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot)
![Gradle](https://img.shields.io/badge/Gradle-Build-02303A?style=for-the-badge&logo=gradle)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)
![Status](https://img.shields.io/badge/STATUS-EM%20DESENVOLVIMENTO-success?style=for-the-badge)

---

# 📌 Índice

- [Visão Geral](#visão-geral)  
- [Arquitetura](#arquitetura)  
- [Tecnologias Utilizadas](#tecnologias-utilizadas)  
- [Funcionalidades](#funcionalidades)  
- [Estrutura do Projeto](#estrutura-do-projeto)  
- [Modelagem do Banco](#modelagem-do-banco)  
- [Instalação e Execução](#instalação-e-execução)  
- [Documentação da API](#documentação-da-api)  
- [Seeds de Desenvolvimento](#seeds-de-desenvolvimento)  
- [Principais Endpoints](#principais-endpoints)  
- [Fluxos Internos](#fluxos-internos)  
- [Roadmap](#roadmap)  
- [Autor](#autor)

---

# 📘 Visão Geral

O **Church League Championship** é um sistema completo para gerenciar campeonatos internos de futebol da igreja.

Inclui:

- Cadastro de times e jogadores  
- Geração automática de partidas  
- Registro de resultados  
- Artilharia  
- Tabela de classificação  
- Regras de WO  
- Seeds automáticos  
- Docker + Postgres  
- Swagger integrado  

O objetivo é ser um sistema **real e profissional**, não apenas um projeto de estudo.

---

# 🏗️ Arquitetura

```mermaid
flowchart TD
    A[Cliente/Swagger] --> B[Controllers REST]
    B --> C[Services]
    C --> D[Repositories]
    D --> E[(PostgreSQL)]
    C --> F[DTOs]
    D --> G[Entities JPA]

🛠️ Tecnologias Utilizadas
Tecnologia	Função
Java 17	Linguagem
Spring Boot 3.5	Framework
Spring Data JPA	ORM
Hibernate	Provider JPA
PostgreSQL 16	Banco
Docker	Containerização
Gradle	Build
H2	Dev opcional
Swagger / OpenAPI	Documentação
🎯 Funcionalidades
🏟️ Torneios

Criação

Classificação automática

Tabela de artilheiros

Regras de pontos

Regras configuráveis de WO

⚽ Partidas

Geração automática (ida / volta)

Atualização de placar

Registro de artilheiros

Aplicação de WO

Listagem por status

🧍 Jogadores

Cadastro por time

Total de gols

Ranking geral

🏅 Classificação

Pontos

Vitórias/empates/derrotas

Saldo de gols

Critérios de desempate

🧱 Estrutura do Projeto
src/main/java/com/churchleague/championship
│
├── controller
├── service
├── repository
├── dto
└── model

🗄️ Modelagem do Banco
erDiagram
    TEAM ||--o{ PLAYER : possui
    TOURNAMENT ||--o{ MATCH : possui
    MATCH ||--o{ GOAL_EVENT : possui
    TOURNAMENT }o--o{ TEAM : participa

🚀 Instalação e Execução
1️⃣ Clonar o projeto
git clone https://github.com/seuusuario/championship.git
cd championship

2️⃣ Subir o PostgreSQL (Docker)
docker compose up -d


Banco disponível em:

host: localhost

porta: 5432

database: championship

user: postgres

senha: postgres

3️⃣ Rodar o back-end
./gradlew bootRun --args='--spring.profiles.active=postgres'

📚 Documentação da API

Swagger disponível em:

👉 http://localhost:8080/swagger-ui.html

👉 http://localhost:8080/v3/api-docs

🌱 Seeds de Desenvolvimento

O arquivo data.sql inclui:

6 times

Jogadores por time

2 torneios

Inscrições completas

Pronto para gerar a tabela e testar rotas

🔗 Principais Endpoints
🏟️ Torneios
GET    /api/tournaments
GET    /api/tournaments/{id}
GET    /api/tournaments/{id}/standings
GET    /api/tournaments/{id}/scorers

⚽ Partidas
POST   /api/matches/generate/{tournamentId}
GET    /api/matches?tournamentId={id}
PUT    /api/matches/{id}/result-with-scorers
PUT    /api/matches/{id}/wo?winnerSide=HOME

🧍 Jogadores
GET    /api/players/team/{id}
GET    /api/players/ranking
POST   /api/players/team/{id}

🏅 Times
GET    /api/teams
POST   /api/teams
PUT    /api/teams/{id}

🔥 Fluxos Internos
🔄 Geração de Partidas

Busca os times

Gera combinação única

Cria ida

Se returno → cria volta

Salva tudo

📝 Atualização de Resultado

Busca partida

Valida status

Salva placar

Registra artilheiros

Atualiza tabela

Recalcula classificação

🗺️ Roadmap
✔️ Concluído

Backend completo

PostgreSQL + Docker

Seeds

Swagger

Regras do campeonato

🟡 Em andamento

Painel admin Angular

🔵 Futuro

Autenticação JWT

Site público

PDF da tabela

Estatísticas avançadas

👤 Autor

Lucas Bezerra
Desenvolvedor Java / Angular
Criador do Church League Championship
Projeto real e em evolução constante.
