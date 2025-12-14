# Camada Repository

A pasta `repository` contém as interfaces de repositório que fazem a comunicação com o banco de dados utilizando **Spring Data JPA**.

---

## Responsabilidades principais

- Acessar o banco de dados (CRUD, queries específicas, etc.)
- Declarar métodos de consulta usando **métodos de convenção** ou **@Query**
- Trabalhar com as entidades da pasta `entity`

---

## Boas práticas usadas (ou esperadas)

- Cada entidade de domínio importante possui um repository correspondente (ex.: `UserRepository`)
- Não colocar regra de negócio aqui – apenas **acesso a dados**
- Usar `Optional<>` quando o retorno pode não existir
- Manter os nomes dos métodos expressivos (`findByEmail`, `findAllByStatus`, etc.)

---

## Futuras modificações

- Criar novos repositórios para novas entidades
- Adicionar queries customizadas quando necessário
