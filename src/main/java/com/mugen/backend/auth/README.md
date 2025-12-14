# Autenticação (Auth)

A pasta `auth` contém a lógica relacionada a **autenticação e autorização** de usuários.

---

## Responsabilidades principais

- Fluxo de login/registro
- Geração e validação de tokens (como JWT)
- DTOs específicos de autenticação (login request, token response, etc.)
- Integração com `security` para validar usuários/token em cada requisição

---

## Boas práticas usadas (ou esperadas)

- Manter bem separados:
    - Controllers de autenticação
    - Services de autenticação
    - DTOs de login/registro
- Nunca expor dados sensíveis (senhas, tokens em logs, etc.)
- Usar criptografia adequada de senhas (ex.: BCrypt)

---

## Futuras modificações

- Adicionar novos fluxos (refresh token, reset de senha, etc.)
- Refinar resposta de erros de autenticação
