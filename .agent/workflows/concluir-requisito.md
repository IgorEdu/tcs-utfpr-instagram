---
description: "Workflow pós-desenvolvimento para preencher a rastreabilidade e finalizar a feature"
---

Sempre que acionado, siga rigorosamente os passos para realizar o fechamento arquitetural do requisito desenvolvido:

1. **Leitura Estrutural Obrigatória**: Use a tool `view_file` para ler três itens chave:
   a. O `static/requisitos.md` (Para se certificar de que os limites do RNF para o respectivo recurso foram observados).
   b. A spec correspondente (ex: `docs/specs/03-postagens.md`) construída durante o desenvolvimento.
   c. A classe DTO e Controller principal onde as lógicas foram engatadas (use `grep_search` se necessário).
2. **Atualização da Spec Local**: Corrija no arquivo `.md` em `docs/specs` qualquer detalhe que a implementação final divergiu do plano inicial garantindo alta precisão para a equipe.
3. **Mapeamento de Status**: Atualize o arquivo `docs/specs/00-rastreabilidade.md`. Nele, altere os respectivos RFs e RNFs concluídos mudando o status de `[A FAZER]` para `[CONCLUÍDO]` anotando de forma sucinta qual Entity ou Service resolveu aquele item.
4. **Resumo**: Apresente ao usuário formalmente o checklist do que foi garantido na feature finalizada alertando caso parte de algum Requisito original não foi totalmente suportada e necessite voltar à área de Produto.
