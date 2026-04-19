---
description: "Fluxo padrão para desenvolver nova feature: Analisar e criticar Specs primeiro"
---

Sempre que este workflow for acionado, siga APENAS os passos abaixo, estritamente nesta ordem:

1. **Pesquisa Obrigatória**: Use a tool `list_dir` na pasta `docs/specs/` e, em seguida, `view_file` para ler o arquivo de especificação da funcionalidade que o usuário quer desenvolver.
2. **Auditoria e Crítica**: Analise o documento lido. Faltam fluxos de exceção? Falta definição de obrigatoriedade no banco de dados? Existem cenários não cobertos pela regra de negócios?
3. **Atualização da Spec**: Caso a Spec esteja incompleta ou seja possível adicionar melhorias arquiteturais (ex: sugestão de paginação, soft delete), use a tool de edição para **sobrescrever ou melhorar o arquivo da Spec (.md)** lá na pasta `docs/specs/`, incluindo os refinamentos.
4. **Criação do Plano**: Entre no modo de planejamento (`implementation_plan.md`) expondo os pontos fortes da spec, o que você melhorou no arquivo e como você propõe o desenho das classes no código.
5. **Aguarde Feedback**: Somente após a aprovação explícita do usuário, inicie a codificação e a criação da `task.md`.
