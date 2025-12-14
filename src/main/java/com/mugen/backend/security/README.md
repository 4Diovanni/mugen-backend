# Segurança (Security)

A pasta `security` concentra a configuração de **segurança da aplicação**, geralmente em conjunto com a pasta `auth`.

---

## Responsabilidades principais

- Configurar **Spring Security**
- Definir filtros, regras de autorização e autenticação
- Configurar quais endpoints exigem token/JWT e quais são públicos
- Integrar com o mecanismo de autenticação (ex.: JWT implementado em `auth`)

---

## Boas práticas usadas (ou esperadas)

- Centralizar as regras de segurança em classes de configuração
- Manter claro quais rotas são **permitAll** e quais exigem autenticação
- Evitar lógica de negócio aqui (apenas configuração de segurança)

---

## Futuras modificações

- Atualizar regras de acesso conforme novos endpoints surgirem
- Ajustar políticas de segurança conforme a aplicação evoluir
