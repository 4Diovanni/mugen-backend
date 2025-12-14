# Camada Controller

A pasta `controller` contém as classes responsáveis por expor os **endpoints REST** da aplicação.

Aqui é onde o backend conversa com o mundo externo (frontend, outros serviços, etc).

---

## Responsabilidades principais

- Definir **rotas/endpoints** (ex.: `/api/users`, `/api/auth/login`, etc)
- Receber **requisições HTTP** (GET, POST, PUT, DELETE...)
- Validar dados de entrada (ex.: usando `@Valid`)
- Delegar a lógica para a camada de **service**
- Retornar respostas padronizadas (`ResponseEntity`, DTOs, mensagens de erro, etc.)

---

## Boas práticas usadas (ou esperadas)

- Cada controller focado em um **contexto de domínio** (ex.: `UserController`, `AuthController`)
- Não colocar regra de negócio complexa aqui – **regra de negócio fica no service**
- Usar **DTOs** como parâmetros e retornos, evitando expor `entity` diretamente
- Retornar códigos HTTP adequados (`201 CREATED`, `200 OK`, `400 BAD_REQUEST`, `404 NOT_FOUND`, etc.)

---

## Futuras modificações

Quando novos módulos forem criados, a ideia é:

- Criar um novo controller para cada contexto/entidade principal
- Manter o padrão de nomes (ex.: `XxxController`)
- Documentar os endpoints no README principal ou via Swagger
