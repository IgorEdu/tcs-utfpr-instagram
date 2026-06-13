# Matriz de Rastreabilidade de Requisitos

| ID | Descrição Sumária | Status Atual | Localização Código / Observação |
|---|---|---|---|
| **RF01** | Cadastro de Novos Usuários (Obrig. Nome, Bio, User, Pass, Email) e Soft Delete. | `[CONCLUÍDO]` | `UsuarioService.cadastrar()`, `UsuarioCadastroDTO.java` (Limites aplicados) |
| **RF02** | Login JWT validado com IAT incluso no Payload. Bearer Token. | `[CONCLUÍDO]` | `UsuarioController.login()`, `TokenService.java` (`.withIssuedAt()`), `SecurityFilter.java` |
| **RF03** | Logout da aplicação | `[CONCLUÍDO]` | `UsuarioController.logout()`, Redis Blacklist (`TokenBlacklistService`) |
| **RF04** | Seguir e deixar de seguir usuários | `[A FAZER]` | - |
| **RF05** | Restrição de auto-seguir | `[A FAZER]` | - |
| **RF06** | Contagem de Seguidores/Seguindo no perfil | `[A FAZER]` | - |
| **RF07** | Feed restrito aos seguidos | `[A FAZER]` | - |
| **RF08** | Visualizar lista de seguidores/seguindo | `[A FAZER]` | - |
| **RF09** | Edição do Perfil (Restrito aos limites do RNF02) | `[CONCLUÍDO]` | `UsuarioService.atualizar()`, `UsuarioAtualizacaoDTO.java` com validações `@Size` |
| **RF10** | Visualizar perfil público | `[CONCLUÍDO]` | `UsuarioController.obterPorId()` com filtro de Ativo |
| **RNF01** | Comunicação Rest HTTP | `[CONCLUÍDO]` | Controllers Spring Web MVC |
| **RNF02** | Validação Universal dos Campos (RegEx e Sizes de Senha, User, Bio, etc) | `[CONCLUÍDO]` | Ajustado perfeitamente via `jakarta.validation.constraints` nos DTOs |
| **RNF03** | Limite/Formato upload foto (Base64) e Legenda | `[CONCLUÍDO]` | Ajustado na Spec `03-postagens.md` (legenda 5 a 200). Implementado em `CriacaoPostRequestDTO` |
| **RNF04** | Formato Bearer Token no Request Header | `[CONCLUÍDO]` | `SecurityFilter.recuperarToken()` |
| **RNF05-RNF08** | Limitações de Postagens (CRUD, formatos) | `[CONCLUÍDO]` | `Post.java`, `PostService.java`, endpoints em `PostController.java` |
| **RNF09-RNF11** | Validações e Controle de Curtidas | `[CONCLUÍDO]` | `PostService.curtir()`, tabela `curtidas_post` |
| **RNF12-RNF14** | Validações e Restrições de Comentários | `[A FAZER]` | - |
