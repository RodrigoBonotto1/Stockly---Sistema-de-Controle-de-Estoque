# 📦 Stockly – API de Controle de Estoque

Stockly é uma API REST para gerenciamento de estoque, desenvolvida com **Spring Boot**. Permite cadastrar, listar, atualizar e remover produtos, com autenticação via **JWT**, paginação, documentação **Swagger/OpenAPI** e ambiente pronto via **Docker**.

## 🧰 Tecnologias utilizadas

- Java 17
- Spring Boot 3.3.4
- Spring Web
- Spring Data JPA
- Spring Security + JWT (jjwt 0.12.5)
- Bean Validation (Jakarta Validation)
- PostgreSQL 16
- springdoc-openapi (Swagger UI)
- Lombok
- Docker / Docker Compose
- Maven

## ▶️ Como executar com Docker

Pré-requisitos: Docker e Docker Compose instalados.

```bash
docker compose build
docker compose up
```

Para encerrar:

```bash
docker compose down
```

Isso vai subir automaticamente:
- o banco de dados PostgreSQL (`stockly-db`);
- a aplicação Spring Boot (`stockly-api`), na porta `8080`.

Ao iniciar pela primeira vez, o banco é populado automaticamente (seed) com:
- 1 usuário padrão para login;
- 12 produtos de exemplo.

Para derrubar o ambiente:

```bash
docker compose down
```

Para recriar tudo do zero (apagando os dados do volume):

```bash
docker compose down -v
docker compose up --build
```

## 👤 Usuário padrão para autenticação

| Usuário | Senha    |
|---------|----------|
| admin   | admin123 |

## 📑 Acessando o Swagger

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui.html
```

(redireciona automaticamente para `http://localhost:8080/swagger-ui/index.html`)

Para autenticar as rotas protegidas dentro do próprio Swagger, faça login em `/auth/login`, copie o token retornado e clique no botão **Authorize** (🔒) no topo da página, informando:

```
Bearer <seu-token>
```

## 🔐 Autenticação JWT

### Login

```
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**Resposta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin",
  "role": "ADMIN"
}
```

### Usando o token

Nas rotas protegidas (criação, atualização e remoção de produtos), envie o header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

> As rotas de **listagem, busca por ID e busca personalizada** (`GET /products/**`) são públicas. Apenas **criar, atualizar e remover** exigem autenticação.

## 📋 Endpoints principais

| Método | Endpoint                 | Autenticação | Descrição                              |
|--------|---------------------------|:------------:|-----------------------------------------|
| POST   | `/auth/login`              | ❌           | Autentica e retorna o token JWT         |
| GET    | `/products`                 | ❌           | Lista produtos com paginação            |
| GET    | `/products/{id}`            | ❌           | Busca um produto por ID                 |
| GET    | `/products/search`          | ❌           | Busca por nome e/ou categoria           |
| POST   | `/products`                 | ✅           | Cria um novo produto                    |
| PUT    | `/products/{id}`            | ✅           | Atualiza um produto existente           |
| DELETE | `/products/{id}`            | ✅           | Remove um produto                       |

## 📄 Paginação e ordenação

```
GET /products?page=0&size=10
GET /products?page=0&size=10&sort=name,asc
```

## 🔎 Busca personalizada

```
GET /products/search?name=Mouse
GET /products/search?category=Periféricos
GET /products/search?name=Mouse&category=Periféricos
```

## 📦 Exemplos de payloads

### Criar produto — `POST /products`

```json
{
  "name": "Mouse Gamer RGB",
  "sku": "MOU-999",
  "description": "Mouse óptico com iluminação RGB e 6 botões",
  "category": "Periféricos",
  "quantity": 50,
  "price": 89.90
}
```

### Resposta

```json
{
  "id": 13,
  "name": "Mouse Gamer RGB",
  "sku": "MOU-999",
  "description": "Mouse óptico com iluminação RGB e 6 botões",
  "category": "Periféricos",
  "quantity": 50,
  "price": 89.90,
  "createdAt": "2026-07-01T10:15:30",
  "updatedAt": "2026-07-01T10:15:30"
}
```

### Erro de validação — exemplo de resposta

```json
{
  "timestamp": "2026-07-01T10:20:00",
  "status": 400,
  "error": "Erro de validação",
  "message": "Um ou mais campos são inválidos",
  "path": "/products",
  "fields": {
    "name": "O nome é obrigatório",
    "price": "O preço deve ser maior que zero"
  }
}
```

## ⚙️ Principais funcionalidades

- **CRUD completo de produtos**: criação, listagem, busca por ID, atualização e remoção.
- **Paginação e ordenação** nativas via Spring Data (`page`, `size`, `sort`).
- **Autenticação JWT** com login, geração de token e proteção das rotas de escrita.
- **Validações** com Bean Validation em todos os campos de entrada, com mensagens claras em português.
- **Tratamento global de exceções**: nunca expõe stacktrace ao cliente; retorna respostas padronizadas para 400, 401, 403, 404, 409 e 500.
- **Busca personalizada** por nome e/ou categoria do produto.
- **Documentação Swagger/OpenAPI** completa e interativa, com suporte a autenticação Bearer.
- **Seeds automáticas**: usuário admin e 12 produtos criados no primeiro start.
- **Ambiente 100% Dockerizado**: `docker compose up` sobe tudo (API + banco).

## 🖥️ Executando localmente sem Docker (opcional)

1. Suba um PostgreSQL local (ou use o `docker compose up stockly-db`).
2. Configure as variáveis de ambiente (ou use os defaults do `application.yml`):
   ```
   DB_HOST=localhost
   DB_PORT=5432
   DB_NAME=stockly
   DB_USER=stockly
   DB_PASSWORD=stockly123
   ```
3. Execute:
   ```bash
   mvn spring-boot:run
   ```

## 📸 Prints da API funcionando

> Adicione aqui os prints do Swagger, do login e das requisições funcionando (Postman/Insomnia) antes da entrega final.

## 📁 Estrutura do projeto

```
stockly/
├── src/main/java/com/stockly/api/
│   ├── config/          # Segurança, OpenAPI e seed de dados
│   ├── controller/      # Controllers REST (Auth, Product)
│   ├── dto/              # Objetos de entrada/saída da API
│   ├── entity/           # Entidades JPA (Product, User, Role)
│   ├── exception/        # Exceções customizadas e handler global
│   ├── repository/       # Repositórios Spring Data JPA
│   ├── security/         # JWT (filtro, util, entry point)
│   └── service/          # Regras de negócio
├── src/main/resources/
│   └── application.yml
├── Dockerfile
├── docker-compose.yml
└── README.md
```
