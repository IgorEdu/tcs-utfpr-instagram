---
description: "Audita as discrepâncias da Tríade: Regras de Negócio x Swagger x Código Java Vivo"
---

Sempre que invocado, este algoritmo rodará uma varredura para garantir a Qualidade e Aderência Universal de um requisito do sistema apontando os erros de convergência da Tríade Tecnológica do Instagram:

### Etapa 1: Coleta de Fundamentos
1. `view_file`: Leia integralmente o arquivo `static/requisitos.md` e guarde as exigências de negócio na memória.
2. `view_file`: Carregue e leia o arquivo `static/allvesleooorganizati-instagram-api-gerenciamento-de-usuarios-1.0.1-resolved.json` avaliando as limitações e validações do OpenAPI/Swagger construído.
3. `grep_search` / `view_file`: Observe os respectivos *Controllers* e *DTOs* implementados no Código Java.

### Etapa 2: Máquina de Cruzamento Crítico
4. Compare os 3 cenários isoladamente apontando problemas de aderência como por exemplo:
   - Um campo determinado com limite de 120 caracteres no `requisitos.md`, mas que está documentado como 150 no `OpenAPI` e possui anotação de limite divergente ou nem possui `@Size` nos `DTOs`.
   - Um código de erro que deveria ser *401* na especificação e o Java devolve *403*.
   - A requisição Swagger não prevê obrigatoriedade de algum campo, o Código também não, mas a Regra de Negócio diz que o campo deve ser obrigatório.

### Etapa 3: Relatoria e Geração de Artefato (Saída)
5. Ao concluir o mapeamento exaustivo, utilize a tool **write_to_file** habilitando o campo **IsArtifact = true** para criar um novo relatório chamado `relatorio-divergencias-auditoria.md` que possuirá o seguinte esqueleto:
   - Resumo das análises executadas
   - Tabela de **Divergências Encontradas** contendo: 
         | Req Ref. | O Que Esperava o Produto | O Que a Documentação Api Afirma | O Que o Código Real Aplica | Ação Sugerida |
   - Grau de Risco e Conclusão

6. Reporte ao usuário para tomar ação imediata se deseja consertar pelo código, pela doc de API ou negociando o Requisito original.
