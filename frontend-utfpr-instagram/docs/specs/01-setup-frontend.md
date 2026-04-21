# Spec: Setup Frontend

## 1. Descrição Geral
Funcionalidade base para inicialização e estruturação do projeto de Front-End utilizando Vue.js. O objetivo é estabelecer a arquitetura base que consumirá a API REST Spring Boot, preparando um ecossistema sólido para autenticação com JWT e gerenciamento dinâmico de interface de usuários.

## 2. Tecnologias Escolhidas
A base do projeto front-end será montada utilizando as seguintes ferramentas oficiais para as melhores práticas:

- **Framework Core**: Vue 3 (Composition API).
- **Build Tool**: Vite (Rápido, oficial do ecossistema e com Hot e Reload instantâneo).
- **Roteamento**: Vue Router (Para um Single Page Application limpo: login, cadastro e feed).
- **Gerenciamento de Estado**: Pinia (Para armazenar tokens de sessão e estado global de autenticação).
- **Client HTTP**: Axios (Para consumir a API de backend na porta 8080 ou superior).
- **Linting e Formatação**: ESLint + Prettier.
- **Estilo**: CSS Vanilla (com variáveis nativas e layouts modernos focados no design estético e responsivo, evitando overhead de frameworks desnecessários).

## 3. Estrutura de Diretórios Inicial
O pacote originado do Vite deve ter seus componentes e lógicas organizados da seguinte maneira em `src/`:

- `/assets`: Imagens globais, fontes e e o `index.css` de estilos base com Paleta de Cores.
- `/components`: Componentes visuais da UI reutilizáveis (ex. Botões Premium, Formulários, Cards).
- `/router`: Regras de rotas e "Navigation Guards" (impedir acesso sem token).
- `/stores`: Lojas da Pinia (ex. `authStore.js` para gerenciar login JWT e usuários ativos).
- `/views`: Telas principais e completas acessíveis por rota (ex. `LoginView.vue`, `HomeView.vue`).
- `/services` (Adicional): Encapsulamento das chamadas com o Axios para o back-end (ex. `usuarioService.js` mapeando endpoints como `/usuarios`).

## 4. Regras de Negócio e Convenções Front-End

- **Segurança (Sessão)**: O token JWT recebido no login só deve transitar através de estados restritos no `Pinia` e armazenado localmente caso haja interesse em persistir sessão (ex: via `localStorage` e interceptors do Axios). Nenhum dado ultra-sensível, como senhas do usuário, é mantido no client-side após o transporte.
- **Requisições Stateless (Axios Interceptors)**: Todo request às rotas do backend (como buscar perfis de amigos) deverá injetar automaticamente o `Bearer <token>` no Header de Autorização caso o usuário possua sessão válida no `authStore`. Respostas do tipo `401 Unauthorized` ou `403 Forbidden` do backend deverão redirecionar o usuário diretamente para a tela de `/login`.
- **Estética Premium**: Todos os botões, modais e transições de feed em componentes Vue deverão aderir a um padrão requintado, com feedback visual ao usuário, paleta de cores harmoniosa, fontes definidas (ex. do Google Fonts) e micro-animações (ex via pseudo-classes `:hover` e `transition` no CSS) entregando aspecto de produto final.
