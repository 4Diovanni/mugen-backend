# Enums

A pasta `enums` contém **enumerações** utilizadas no domínio da aplicação.

---

## Responsabilidades principais

- Representar valores fixos (status, tipos, categorias, etc.)
- Ajudar a manter consistência e evitar strings soltas no código

---

## Boas práticas usadas (ou esperadas)

- Dar nomes claros para os enums e seus valores
- Usar enums nas entidades e DTOs sempre que possível, ao invés de `String` solta
- Evitar colocar lógica complexa dentro dos enums (manter simples)

---

## Futuras modificações

- Adicionar novos enums conforme o domínio ficar mais rico
- Ajustar valores conforme regras de negócio mudem
