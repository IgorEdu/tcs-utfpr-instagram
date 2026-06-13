# Spec Fundacional: Padrões de Resposta da API (Envelope JSON)

## 1. Visão Geral
Para garantir a consistência das respostas e seguir estritamente o contrato estabelecido no OpenAPI (Swagger), toda requisição realizada nesta API deve ser obrigatoriamente encapsulada em um **Modelo de Envelope**. 

> [!WARNING]
> É estritamente **proibido** retornar DTOs soltos, Arrays crus `[]` ou Strings literais puras soltas na raiz do corpo de respostas do Spring Boot localizadas nas Controllers.

## 2. A Anatomia Geral do Envelope
O chassi universal de requisições de resposta possui as seguintes chaves primárias:

- **`status`**: String, aceitando exclusivamente `"sucesso"` ou `"erro"`.
- **`codigo`**: String em *uppercase* e *snake_case*. Trata-se de um identificador rastreável pelo sistema consumidor (Machine-to-Machine). Ex: `"OPERACAO_CONCLUIDA"`, `"DADOS_INVALIDOS"`.
- **`mensagem`**: String contendo a descrição amigável, feedback visual ou o erro em formato nativo, passível de exibição imediata pelo Front-end (Human Readable).
- **`dados`**: Object / Nodo Opcional. Usado apenas em retornos de Sucesso. Ele encapsula e carrega a sua resposta (O objeto de Usuário, A Entidade criada, o Token etc). Em operações estritamente de *Delete* ou *Logout*, pode não vir renderizado.

---

## 3. Padrão de Retorno: Sucesso Unitário (`"sucesso"`)
Aplicável sempre que uma operação única e direta é chamada e concluída (Ex: Cadastro, Edição, Consulta simples por ID e Login).

**Exemplo de Payload Resposta (HTTP 200/201):**
```json
{
   "status": "sucesso",
   "codigo": "USUARIO_ATUALIZADO",
   "mensagem": "Os dados foram alterados com sucesso.",
   "dados": {
       "id": 1,
       "nome_completo": "Aline Silva"
   }
}
```

---

## 4. Padrão de Retorno: Sucesso Paginação / Listagem (`"sucesso"`)
Ao executar operações que retornariam múltiplos resultados (Listas longas na base como `GET /usuarios` ou `GET /feed/`), o nó `dados` assume a forma de controlador numérico envelopando o Array listado.

**Exemplo de Payload Resposta (HTTP 200):**
```json
{
   "status": "sucesso",
   "codigo": "BUSCA_CONCLUIDA",
   "mensagem": "A busca retornou os conteúdos solicitados.",
   "dados": {
       "total": 105,
       "pagina": 1,
       "limite": 10,
       "itens": [
            { "id": 1, "owner": "aline" },
            { "id": 2, "owner": "joão" }
       ]
   }
}
```

---

## 5. Padrão de Retorno: Falhas e Erros (`"erro"`)
Aplicável em falhas controladas, disparado exclusivamente pelas capturas sistêmicas da arquitetura em nosso `@RestControllerAdvice`.
*Atenção: Ausência total do nó `dados` para proteger vazamento de chassi backend.*

**Exemplo de Payload Resposta (HTTP 4XX, 5XX):**
```json
{
   "status": "erro",
   "codigo": "FORMULARIO_VIOLADO",
   "mensagem": "A biografia informada excede os limites estabelecidos de 150 caracteres."
}
```

## Diretriz para Autores de API e Novas Especificações
As próximas especificações elaboradas ao decorrer do projeto (ex: `03-postagens.md`) estão isentas de detalhar o envelope nas saídas das chamadas! Elas apenas focarão nas propriedades internas dos Objetos e delegarão esse contrato formal do Payload raiz universal para este arquivo de Base.
