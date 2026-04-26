# Workflow: Desenvolver Front-End (Iterativo)

Este workflow (`/desenvolver-frontend`) guia o agente em como conduzir a criação de novas telas, rotas e regras de negócio no Vue.js integrando as informações pré-existentes.

Aplica-se sempre que o User solicitar a implementação de um novo módulo de interface.

## Passos (Agentic Instructions)

1. **Checar Rastreabilidade (O que construir?):**
   - Leia `docs/rastreabilidade.md` procurando as features mapeadas como `Status Frontend` igual a *:x: Pendente*, mas que tenham o `Status Backend` como *:white_check_mark: Concluído* ou que o User indicou como alvo da sessão.
   - Entenda teoricamente qual é o RF associado lendo esse tópico referenciado em `docs/requisitos.md`.

2. **Checar Contrato da API:**
   - Leia `backend-utfpr-instagram/instagram/src/main/resources/static/api-specs.json` (Trata-se da Single Source of Truth do Backend para contratos).
   - Confirme o Payload que deverá ser enviado e as possíveis Respostas (`200`, `400`, `401`, erro formatado).

3. **Arquitetar a Solução Interna do Vue:**
   - Construir o serviço de integração na pasta `frontend-utfpr-instagram/src/services/` (ex. uso de Axios chamando o Endpoint respectivo).
   - Se for uma rota que exige sessão, certificar-se de passar o Bearer Token obtido do `Pinia` Store na autorização.

4. **Codificação Visual (Template e Estilo Premium):**
   - Criar e/ou Modificar as páginas sob o diretório `views/` e os componentes sob `components/`.
   - Adotar somente **Vanilla CSS** mantendo interatividade fluente (transições e UI luxuosa).

5. **Atualização da Trajetória:**
   - [Obrigatório] Ao concluir com sucesso o módulo de tela, abra novamente a tabela no `docs/rastreabilidade.md` e modifique o ícone do `Status Frontend` do Requisito de *:x: Pendente* para *:white_check_mark: Concluído*.
   - Caso precise testar, peça para o Usuário rodar a UI localmente (`npm run dev`) e verificar as mudanças.
