# DTOs (Data Transfer Objects)

A pasta `dto` contém classes usadas para **troca de dados** entre a API e o mundo externo (requests e responses), desacoplando a representação externa das **entidades internas**.

---

## Responsabilidades principais

- Representar payloads de **entrada** (requests) e **saída** (responses)
- Evitar expor diretamente as entidades do domínio para o cliente
- Facilitar validações com anotações (`@NotNull`, `@Email`, etc.)

---

## Boas práticas usadas (ou esperadas)

- Ter DTOs separados para request/response quando fizer sentido
    - Ex.: `UserRequestDTO`, `UserResponseDTO`
- DTOs focados apenas nos dados que fazem sentido na borda da aplicação
- Conversão entre DTO ↔ Entity feita no service ou com mappers utilitários

---

## Futuras modificações

- Criar novos DTOs à medida que endpoints forem adicionados
- Refatorar DTOs quando a API evoluir
