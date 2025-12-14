# Exceções (Exception)

A pasta `exception` concentra **exceções customizadas** e, possivelmente, **handlers globais** para tratamento de erros na API.

---

## Responsabilidades principais

- Definir exceções específicas do domínio (ex.: `UserNotFoundException`)
- Criar um handler global (ex.: `@ControllerAdvice`) para padronizar respostas de erro
- Retornar mensagens, códigos de status e payloads de erro consistentes

---

## Boas práticas usadas (ou esperadas)

- Usar exceções customizadas para cenários conhecidos de negócio
- Centralizar o tratamento em um único handler global
- Nunca retornar stack traces diretamente para o cliente
- Mapear exceções para códigos HTTP adequados (`404`, `400`, `409`, etc.)

---

## Futuras modificações

- Adicionar novas exceções conforme novas regras de negócio surgirem
- Melhorar mensagens de erro para facilitar debugging e uso da API
