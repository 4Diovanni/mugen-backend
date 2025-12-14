# Entidades (Entity)

A pasta `entity` contém as **entidades de domínio** que representam tabelas do banco de dados (via JPA/Hibernate).

---

## Responsabilidades principais

- Mapear as tabelas do banco de dados (anotações `@Entity`, `@Table`, etc.)
- Definir colunas (`@Column`, `@Id`, `@GeneratedValue`, etc.)
- Representar o modelo de domínio da aplicação (usuários, personagens, itens, etc.)

---

## Boas práticas usadas (ou esperadas)

- Utilizar tipos adequados para campos (String, int, enums, etc.)
- Mapear relacionamentos (`@OneToMany`, `@ManyToOne`, `@ManyToMany`, etc.) de forma clara
- Evitar colocar regra de negócio pesada na entidade (regra de negócio fica no service)
- Usar **enums** para valores fixos (estado, tipo, categoria, etc.)

---

## Futuras modificações

- Adicionar novas entidades conforme o domínio crescer
- Refinar mapeamentos e relacionamentos entre entidades
