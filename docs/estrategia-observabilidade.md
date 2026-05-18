# Estratégia de Observabilidade - Instagram

Este documento define a estratégia e arquitetura para a implementação do sistema de observabilidade na aplicação, focado em monitorar o tráfego, sessões ativas e origens dos usuários.

## 1. Monitoramento de Usuários Logados e Seus IPs

Como nossa aplicação utiliza JWT (arquitetura stateless), o servidor por padrão não mantém o estado da sessão. Para rastrear **quem está logado** e os **IPs** utilizados, vamos adotar a seguinte abordagem utilizando o **Redis** (que já está integrado ao projeto):

- **Registro de Sessão no Login:** 
  - Ao realizar o login com sucesso (endpoint `POST /usuarios/login`), o backend extrairá o endereço IP do usuário da requisição (ex: cabeçalhos `X-Forwarded-For` ou `HttpServletRequest.getRemoteAddr()`).
  - Um registro temporário será salvo no Redis (ex: chave `active_session:{usuarioId}:{tokenId}`) contendo as informações: ID do usuário, Username, IP de origem e timestamp de login.
  - O TTL (Time To Live) deste registro será exatamente igual ao tempo de expiração do JWT gerado.
- **Encerramento da Sessão (Logout):**
  - Ao fazer logout (endpoint `POST /usuarios/logout`), além de enviar o token para a *blacklist* atual, também removeremos o registro correspondente das sessões ativas no Redis.
- **Consulta de Sessões Ativas:**
  - Criação de um endpoint restrito para administradores/auditoria (ex: `GET /admin/sessoes`) que varre as chaves de sessões ativas no Redis e retorna a lista de usuários logados e seus respectivos IPs.

## 2. Rastreamento de Requisições de Entrada e Saída (Backend)

No backend (Spring Boot), precisamos interceptar e registrar todas as interações.

- **Filtro de Interceptação Global (Custom HTTP Filter):**
  - Implementaremos um `Filter` ou `HandlerInterceptor` no Spring.
  - **Entrada:** Registrará o método (GET, POST, etc), URI, IP de origem e headers relevantes assim que a requisição chegar.
  - **Saída:** Registrará o status HTTP da resposta e o tempo de processamento (latência).
- **Spring Boot Actuator:**
  - Adição da dependência `spring-boot-starter-actuator`.
  - Habilitação do `HttpExchangeRepository` nativo (Spring Boot 3) para expor as últimas requisições no endpoint `/actuator/httpexchanges`.
- **Logs Estruturados:**
  - Configurar o SLF4J/Logback para emitir os logs das requisições em um formato parseável, facilitando futura integração com ferramentas como Grafana/Loki ou ELK Stack.

## 3. Rastreamento de Requisições de Entrada e Saída (Frontend)

No frontend (Vue.js), todas as requisições para a API já estão centralizadas no arquivo `src/services/api.js` que utiliza a Fetch API nativa. 

- **Interceptador no Fetch (api.js):**
  - Adicionar um wrapper ao redor do `fetch`.
  - **Antes da requisição (Saída):** Logar o início da requisição, método, URL de destino e payload (se aplicável).
  - **Depois da resposta (Entrada):** Logar o status HTTP recebido, dados retornados e o tempo que a requisição levou (ms).
- **Exibição / Coleta:**
  - Inicialmente, os logs serão enviados de forma estruturada para o `console.log/warn/error` do navegador.
  - (Opcional - Futuro) Criar um endpoint no backend (`POST /telemetria`) onde o frontend envia em lote métricas que o backend repassa ao Prometheus.

## 4. Infraestrutura de Observabilidade (Prometheus e Grafana via Docker)

Para centralizar, armazenar e visualizar essas informações (especialmente do backend), utilizaremos uma stack padrão de mercado: **Prometheus** e **Grafana**. Ambas as ferramentas serão provisionadas através do nosso arquivo `docker-compose.yml` existente.

- **Prometheus (Coleta de Métricas):**
  - Será configurado em um container Docker para fazer o *scraping* periódico (ex: a cada 5 segundos) do endpoint `/actuator/prometheus` da aplicação Spring Boot.
  - O Prometheus armazenará o histórico do tempo de resposta de cada endpoint (requisições de entrada e saída), contagem de erros HTTP, e nossas métricas customizadas de usuários e sessões.
- **Grafana (Visualização):**
  - Rodará em outro container, conectando-se automaticamente ao Prometheus como fonte de dados (Datasource).
  - Criaremos um **Dashboard Customizado** para exibir visualmente:
    - O número total de sessões ativas (usuários logados no momento).
    - Tabelas exibindo os IDs/Nomes de usuários logados e seus IPs recentes.
    - Gráficos de tráfego de requisições (QPS, taxa de sucesso/erro) do backend.
    - Gráficos de latência/tempo de resposta (percentis P95, P99).

*Nota de Arquitetura:* Como o Prometheus não é a ferramenta ideal para armazenar strings de alta cardinalidade (como milhões de IPs únicos), para a escala deste projeto acadêmico usaremos *Gauges/Métricas Customizadas* do Micrometer para exportar essas tags (`user_name`, `ip_address`), tornando viável a construção rápida do dashboard no Grafana.

## 5. Latência Assíncrona de Telemetria (Delay Esperado)

Em sistemas de observabilidade modernos que não afetam a performance do backend, existe um comportamento intrínseco de *delay* entre a ação real do usuário e o dado aparecer no dashboard do Grafana. Entender e ajustar essas janelas é fundamental:

- **Mecânica de Pull do Prometheus:** O Prometheus raspa as métricas (`/actuator/prometheus`) periodicamente. O padrão é a cada 5s a 15s. O dashboard do Grafana também precisa de um intervalo de *refresh* semelhante (5s). Isso gera um atraso total de até 10-20 segundos.
- **Janela de Cálculo (Rate) no Grafana:** Gráficos como a Taxa de Requisições utilizam o operador PromQL `rate(...[1m])` por padrão em produção. Como é uma média móvel de 60 segundos, o gráfico ganha uma rampa suave e não mostra picos irreais.
- **Buffers de Logs do Loki:** Para evitar milhares de chamadas HTTP simultâneas paralisando o Java, a biblioteca `loki-logback-appender` acumula as mensagens na memória em *batches* e despacha de uma vez só a cada X milissegundos.

**Ajuste para Ambiente de Desenvolvimento (Reativo):**
Para garantir que a resposta no Grafana seja imediata durante testes na máquina local, adotamos os seguintes ajustes agressivos (não recomendados para produção pesada):
1. Grafana: O cálculo da média do tráfego usa janelas extremamente curtas (`[15s]`).
2. Loki Appender: O tempo de espera do Buffer (`batchTimeoutMs`) foi abaixado de 60.000ms (padrão) para apenas 1.000ms. O log pinga instantaneamente no Loki.

## Próximos Passos (Plano de Ação Atualizado)
1. Atualizar o `pom.xml` para incluir o `spring-boot-starter-actuator` e a dependência `micrometer-registry-prometheus`.
2. Adicionar os serviços do **Prometheus**, **Loki** e **Grafana** no `docker-compose.yml`, criando os arquivos básicos de configuração (ex: `prometheus.yml`).
3. Criar Filtro HTTP / Configuração de Métricas no Spring Boot para interceptar requisições e expor o número de usuários logados e IPs via tags do Micrometer.
4. Refatorar o `api.js` no frontend para garantir a visibilidade das requisições via console.
5. Iniciar o Docker Compose, acessar o Grafana e montar o Dashboard inicial de Observabilidade.
