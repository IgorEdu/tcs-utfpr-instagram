# Spec: Gerenciamento de Postagens

## 1. Descrição Geral
Funcionalidade para permitir que os usuários criem postagens de fotos, interajam através de curtidas, editem legendas e removam suas publicações.

## 2. Entidade de Banco de Dados (`Post`)
Esta spec estabelece as definições que o módulo (ainda não implementado) deverá possuir para respeitar a documentação da API e as regras de negócio alinhadas:
- `id`: Long / `BIGSERIAL` (Chave Primária)
- `id_usuario`: Long / Chave Estrangeira referenciando a tabela `usuarios` (Dono do post)
- `imagem_base64`: String (`TEXT`) / Imagem no formato string Base64 (Obrigatório, limite 5MB real ~ 6.6MB Base64)
- `legenda`: String / Texto acompanhando a foto, limite de 200 caracteres (Obrigatório, min 5)
- `ativo`: Boolean / Padrão `true` (Soft delete)
- `data_criacao`: Timestamp
- `data_atualizacao`: Timestamp

*Nota:* Curtidas deverão possuir tabela auxiliar `curtidas_post` (`id_usuario`, `id_post`).

## 3. Interfaces HTTP (Endpoints REST)

O recurso de postagens está aninhado aos usuários, logo o caminho base é: `/usuarios/{id-usuario}/posts`

- [ **POST** `/` ] **(Criar Post)**
  - *Payload*: `imagem` (Base64) e `legenda` são obrigatórios. Legenda entre 5 e 200 caracteres.
  - *Restrições*: Aceitar formatos equivalentes a JPG, JPEG, PNG (em Base64).
  - *Fluxo Exceção*: Usuário não encontrado retorna `404 NOT FOUND`. Se a validação falhar retorna `400 BAD REQUEST`.
  - *Retorno*: `201 CREATED` com `SucessoSimples` (`codigo: "OPERACAO_SUCESSO"`).

- [ **GET** `/` ] **(Listar Posts)**
  - *Retorno*: `200 OK` com a listagem dos posts (ativos) e quantidade de curtidas de cada um. O DTO de resposta encapsula a lista de posts.
  - *Fluxo Exceção*: Se o usuário pesquisado não existir, retornar `404 NOT FOUND`.
  - *Melhoria Arquitetural (Nota)*: Como a lista de posts tende ao infinito, é altissimamente recomendado preparar a infraestrutura para Paginação no futuro.
  
- [ **PATCH** `/{id-post}` ] **(Atualizar Post)**
  - *Payload*: `legenda`
  - *Restrições*: O usuário só pode alterar a legenda da postagem (não a imagem). A legenda continua respeitando de 5 a 200 caracteres.
  - *Fluxo Exceção*: Tentar atualizar post excluído (ativo=false) ou inexistente: `404 NOT FOUND`. Tentar atualizar post de outro usuário: `403 FORBIDDEN` ou `AcessoNegadoException`.
  - *Retorno*: `200 OK`.

- [ **DELETE** `/{id-post}` ] **(Remover Post)**
  - *Restrições*: Soft delete no post (setar `ativo = false`). Remove o post por completo (não pode excluir apenas a foto).
  - *Fluxo Exceção*: Tentar deletar post excluído (ativo=false) ou inexistente: `404 NOT FOUND`. Tentar deletar post de outro usuário: `403 FORBIDDEN`.
  - *Retorno*: `200 OK` (`codigo: "OPERACAO_SUCESSO"`).

- [ **POST** `/{id-post}` ] **(Curtir Post)**
  - *Retorno*: `201 CREATED` contendo a resposta de curtida (`id_usuario`, `id_post`).
  - *Restrições*: O usuário só pode curtir uma vez. Se enviar novamente, o comportamento deve computar como "descurtir" (Remover curtida).

## 4. Regras de Negócio Inegociáveis
- **Autenticação**: O sistema não deverá permitir que usuários não autenticados criem, editem, deletem, listem ou curtam postagens. A rota é protegida pelo JWT.
- **Autorização (Dono)**: A edição e exclusão de um post só pode ser realizada pelo próprio usuário criador (ID do token = `id-usuario`).
- **Resolução de Conflitos (Legenda)**: Conforme alinhamento entre OpenAPI e Regras, a legenda **deixa de ser opcional**. Ela agora é estritamente **obrigatória**, necessitando possuir entre **5 e 200 caracteres**.
