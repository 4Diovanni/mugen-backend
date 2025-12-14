# Mugen Backend

Backend do projeto **Mugen**, desenvolvido em **Java + Spring Boot**, responsável por disponibilizar APIs para autenticação, gerenciamento de usuários, entidades de domínio do jogo e demais funcionalidades de negócio.

Este repositório concentra toda a lógica do backend, organização em camadas (controller, service, repository etc.), segurança, tratamento de exceções e integração com o banco de dados.

---

# Tecnologias principais

* **Java 21** (ou versão definida no `pom.xml`)
* **Spring Boot**

  * Spring Web
  * Spring Data JPA
  * Spring Security
* **JWT** para autenticação/autorização (pasta `auth` / `security`)
* **Maven** como gerenciador de dependências (`mvn` / `./mvnw`)
* **Docker** para containerização (`Dockerfile`, `.dockerignore`)
* Banco de dados relacional (definido nas configs do projeto)

---

# Estrutura de pastas (src/main/java/com/mugen/backend)

```
com.mugen.backend
├── MugenBackendApplication.java
├── auth
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── security
└── service
```

---

# Visão geral das camadas

* **auth**
  Fluxo de autenticação: registro, login, geração/validação de tokens, refresh tokens etc.

* **config**
  Configurações gerais da aplicação (CORS, beans, mapeamentos globais, configurações do Spring, propriedades).

* **controller**
  Controllers que expõem as APIs REST. Endpoints que o frontend/consumidores chamam.

* **dto**
  Data Transfer Objects — modelos usados para entrada/saída (requests/responses) evitando expor entidades diretamente.

* **entity**
  Entidades de domínio mapeadas para o banco (JPA/Hibernate).

* **enums**
  Enumerações do domínio (tipos, estados, categorias).

* **exception**
  Exceções customizadas e handlers globais para padronizar respostas de erro.

* **repository**
  Interfaces do Spring Data JPA para acesso ao banco (CRUD, consultas customizadas).

* **security**
  Configurações de segurança, filtros, providers, integrações com JWT (trabalha em conjunto com `auth`).

* **service**
  Regras de negócio — aqui fica a lógica principal da aplicação.

---

# Como executar o projeto

## Pré-requisitos

* Java (versão compatível com o projeto)
* Maven (ou usar o wrapper `./mvnw`)
* Banco de dados configurado (ver `application.properties` / `application.yml`)
* (Opcional) Docker para rodar o container

## Executando localmente

1. Clonar o repositório:

```bash
git clone https://github.com/4Diovanni/mugen-backend.git
cd mugen-backend
```

2. Rodar com Maven (usar wrapper se disponível):

```bash
# com wrapper (recomendado, se existir)
./mvnw spring-boot:run

# ou com maven instalado
mvn spring-boot:run
```

A aplicação, por padrão, sobe em:
`http://localhost:8080`

(consulte os controllers do projeto para ver os endpoints disponíveis)

---

# Docker

O repositório possui um `Dockerfile` pronto.

**Build da imagem:**

```bash
docker build -t mugen-backend .
```

**Rodando o container:**

```bash
docker run -p 8080:8080 --name mugen-backend mugen-backend
```

---

# Convenções de código / arquitetura

* Estrutura clara de camadas: **Controller → Service → Repository**
* Utilizar **DTOs** para requests/responses, evitando expor entidades diretamente.
* Exceções customizadas + handler global (`@ControllerAdvice`) para padronizar erros.
* Segurança centralizada nas pastas `auth` e `security` (JWT, filtros, providers).
* Uso de **Enums** para valores fixos do domínio.
* Seguir convenções de nomes e práticas comuns do Spring Boot (component scan, profiles, properties).

---

# Próximos passos / futuras melhorias

* Documentar endpoints com **OpenAPI / Swagger**.
* Adicionar READMEs internos por domínio (usuários, personagens, itens, etc.).
* Exemplos de requests/responses (curl / Postman / collection).
* Implementar testes unitários e de integração (JUnit, MockMvc).
* Melhorar CI/CD (builds automáticos, testes, imagens Docker no registry).
* Políticas de versionamento de API (v1, v2...) e migrações/seed do banco.

---

# Observações finais

* Consulte os controllers para descobrir todos os endpoints e modelos de request/response.
* Mantenha `application.properties`/`application.yml` fora do controle de versão quando contiver segredos (use variáveis de ambiente ou um vault).
