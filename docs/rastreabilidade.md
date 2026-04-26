# Matriz de Rastreabilidade (Features x Endpoints x UI)

Esta tabela consolida o status de implementação dos Requisitos Funcionais descritos na nossa base (vistos em `docs/requisitos.md`). Através dela, as equipes de desenvolvimento (ou assistentes autônomos) avaliam rapidamente o que foi concluído tanto no Java (Backend) quanto no Vue (Frontend).

## Requisitos Funcionais (RF) - Instagram

| ID | Nome do Requisito / Feature | Rota Associada (API) | Status Backend (Java) | Status Frontend (Vue) | Observações |
|---|---|---|---|---|---|
| **RF01** | Cadastro de Usuários | `POST /usuarios` | :white_check_mark: Concluído | :x: Pendente | Senha/Hashes incluídas. |
| **RF02** | Login / Autenticação JWT | `POST /usuarios/login` | :white_check_mark: Concluído | :x: Pendente | Retorna token Bearer. |
| **RF03** | Logout | `POST /usuarios/logout` | :white_check_mark: Concluído | :x: Pendente | Uso de Blacklist Redis. |
| **RF04** | Dinâmica: Seguir Usuários | *A Definir* | :x: Pendente | :x: Pendente | |
| **RF05** | Regra: Não seguir a si mesmo | *Inerente ao RF04* | :x: Pendente | :x: Pendente | |
| **RF06** | Contagem Seguidores/Seguindo | *A Definir* | :x: Pendente | :x: Pendente | Visível no Perfil. |
| **RF07** | Feed de Postagens | *A Definir* | :x: Pendente | :x: Pendente | Exibe apenas quem sigo. |
| **RF08** | Ver Lista de Seguidores | *A Definir* | :x: Pendente | :x: Pendente | |
| **RF09** | Editar Próprio Perfil | `PATCH /usuarios/{id}` | :white_check_mark: Concluído | :x: Pendente | Atualização parcial. |
| **RF10** | Ver Perfil Público | `GET /usuarios/{id}` | :white_check_mark: Concluído | :x: Pendente | Retorna atributos do alvo. |
| **RF11/List** | *Listagem Paginada* (Implícito) | `GET /usuarios` | :white_check_mark: Concluído | :x: Pendente | Descoberta ou Explorar. |

---

**Legenda de Desenvolvimento:**
- :white_check_mark: **Concluído**: Código pronto, validado e merge efetuado.
- :construction: **Em Andamento**: Sendo desenvolvido na branch/sprint atual.
- :x: **Pendente**: Sem implementação ou aguardando priorização.

> *Nota: Todas as rotas implementadas no backend e com status concluído devem obrigatoriamente possuir contrato equivalente documentado no JSON oficial gerador (`backend-utfpr-instagram/instagram/src/main/resources/static/api-specs.json`).*
