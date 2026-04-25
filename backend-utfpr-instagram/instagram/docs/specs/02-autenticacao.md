# Spec: Autenticação & Segurança JWT

## 1. Descrição Geral
Funcionalidade para proteger as rotas da API, garantindo que apenas usuários da plataforma logados e portando um token JWT válido consigam acessar os pacotes de dados. Esta spec abrange a criação desse token e a barreira global.

## 2. Requisitos de Barreira (Filtro Global)
- O sistema interceptará **todas as conexões HTTP** para realizar validação de credencial (Bearer Token).
- Requisições sem Token, ou com Token expirado/adulterado receberão código The `401 Unauthorized`.
- **White-list Inicial (Sempre Públicas)**:
  - `POST /usuarios` (Permitir o registro de conta por visitantes).
  - `POST /usuarios/login` (Permitir o credenciamento).
  - `/v3/api-docs/**` e `/swagger-ui/**` (Visibilidade da documentação estática ou dinâmica).

## 3. Endpoints (API REST)

| Método | Rota | Descrição | Regras Adicionais |
|---|---|---|---|
| POST | `/usuarios/login` | Gerar Token Web | Recebe payload de `usuario` e `senha`. Compara com as hashes existentes via verificação assíncrona. Em caso de sucesso, devolve o Token JWT. Em caso de falha de credenciais, acusa `401`. |
| POST | `/usuarios/logout` | Revogar Token | Recebe a requisição com o token ativo. A API registrará este token específico em uma `Blacklist` de Cache (provida via **Redis**) para invalidação imediata da sessão antes de sua expiração natural. |

*(Nota de Arquitetura: **Não existirá renovação (Refresh Token)** na plataforma. Quando o Token JWT primário atingir o limite do seu tempo de vida e expirar, a API fatalmente retornará `401 Unauthorized` para as requisições subsequentes do usuário. Caberá à aplicação consumidora/Frontend forçar que a pessoa passe pelo Login novamente para emitir uma chave limpa e fresca).*

## 4. Gestão de Tokens (Serviço Especializado)
- O token gerado usa encriptação simétrica (HMAC256) com uma variável de segredo (`secret`) da aplicação.
- Ele deve injetar o `Username` (subject) do usuário como propriedade interna e principal (para não ser mascarado/corrompido pelo lado cliente).
- Expiração primária do token não deve ultrapassar 2 horas em modo de produção por segurança.
- **Validação com Blacklist (Cache Redis)**: A invalidação stateful existirá no projeto via banco em memória. Durante as requisições em rotas protegidas, antes de verificar se o token está no prazo de validade matemática, o Filtro deve obrigatoriamente checar via Interface se o token encontra-se registrado na `Blacklist` do Redis. Caso esteja, o acesso será negado com `401 Unauthorized` de imediato.
- **Controle de Acesso (Roles / Permissões)**: A plataforma possui perfil de "Administrador" e de "Usuário" comum, controlados pelo campo booleano `is_admin`. A autorização deve levar em consideração essa flag (mapeada como *authority* no Spring Security, por exemplo). Módulos de RBAC básicos devem ser implementados para que endpoints específicos possam ser restritos a administradores.
