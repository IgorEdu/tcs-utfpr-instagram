# Spec: Gerenciamento de Usuários

## 1. Descrição Geral
Funcionalidade base para permitir gerenciar o ciclo de vida dos usuários na plataforma (Cadastro, Edição, Busca e Remoção).

## 2. Entidade de Banco de Dados (`Usuario`)
A tabela `usuarios` deve ser criada via migração e possuir os respectivos campos associados ao Java:
- `id`: Long / `BIGSERIAL` (Chave Primária)
- `nome_completo`: String / Obrigatório
- `usuario`: String / Identificador de username (Obrigatório, Único)
- `email`: String / E-mail válido (Obrigatório, Único)
- `biografia`: String / Máx 150 caracteres (Opcional)
- `foto_url`: String (Opcional)
- `senha`: String / Armazenar formato Hash (Obrigatório)
- `ativo`: Boolean / Padrão `true`

## 3. Interfaces HTTP (Endpoints REST)

O recurso tem como caminho base: `/usuarios`

- [ **POST** `/` ] **(Cadastrar)** 
  - *Payload*: `nome_completo`, `usuario`, `email` e `senha` são obrigatórios. `biografia` e `foto` opcionais. Usar `spring-validation` (`@NotBlank`, `@Email`).
  - *Retorno*: `201 CREATED`. Não exibir a senha na saída.

- [ **GET** `/` ] **(Listar)** 
  - *Payload*: Vazio
  - *Retorno*: `200 OK`. Retorna a lista contendo apenas usuários ativos.

- [ **GET** `/{id}` ] **(Obter por ID)** 
  - *Retorno*: `200 OK` com dados ou `404 NOT FOUND` se usuário não for achado ou `ativo = false`.

- [ **PATCH** `/{id}` ] **(Atualização Parcial)**
  - *Payload*: Todo arquivo opcional. Atualizar apenas os campos que o Frontend enviar.
  - *Retorno*: `200 OK` refletindo as alterações.

- [ **DELETE** `/{id}` ] **(Remover)**
  - *Ação*: Executar **Soft Delete** em vez de exclusão física na linha (Mudar campo `ativo` pra falso).
  - *Retorno*: `200 OK` ou `404 NOT FOUND`.

## 4. Regras de Negócio Inegociáveis (Service Layer)
- **Hash de Senha**: Ao cadastrar e ao alterar, a senha fornecida pelo cliente não pode parar no banco de dados livremente. Usar mecanismo de Hash via classe de utilidade (`CriptografiaService`).
- **Validação de Unicidade**: Tanto POST quanto PATCH não devem permitir que o sistema cadastre duas pessoas usando o mesmo `@username` ou o mesmo `e-mail`. Acusar `400 Bad Request` ou `409 Conflict`.
