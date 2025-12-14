# Camada Service

A pasta `service` concentra a **lógica de negócio** do sistema.

Enquanto o controller lida com HTTP e rotas, o `service` lida com **regras de negócio**, validações mais complexas e orquestração entre repositórios, entidades e outros serviços.

---

## Responsabilidades principais

- Implementar as **regras de negócio** do domínio
- Coordenar chamadas a **repositories** e outras camadas
- Tratar cenários de erro e disparar exceções customizadas
- Converter/usar DTOs e entidades quando necessário

---

## Boas práticas usadas (ou esperadas)

- Uma interface + implementação para serviços mais importantes (ex.: `UserService` / `UserServiceImpl`)
- Manter métodos coesos, cada um com uma única responsabilidade clara
- Não ter código de HTTP aqui (isso fica no controller)
- Não acessar o banco diretamente aqui – sempre via `repository`

---

## Futuras modificações

- A cada novo contexto de negócio, criar um service correspondente
- Centralizar regras de negócio aqui para facilitar testes e manutenção
