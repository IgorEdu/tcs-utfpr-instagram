# backend-utfpr-instagram

Este README descreve como iniciar a documentação OpenAPI (Swagger) para o módulo `instagram`.

Requisitos
- Java 21
- Maven 3.8+

Dependência usada
- springdoc-openapi-starter-webmvc-ui (já adicionada ao `pom.xml`)

Como rodar
1. Navegue até o módulo:

   cd instagram

2. Build e run com Maven:

   mvnw.cmd spring-boot:run

Acessando a documentação
- Swagger UI (interface web): http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

Observações
- A porta padrão é 8080; ajuste via `application.properties` se necessário.
- Se quiser personalizar mais (grupos, security, etc.), podemos adicionar configurações extras.

