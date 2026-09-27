# Backend - Controle Financeiro

## 1. Pré-requisitos
- Java 17 instalado (`java -version`)
- Maven instalado (`mvn -version`)
- PostgreSQL instalado e rodando localmente

## 2. Criar o banco e o usuário no PostgreSQL

```sql
CREATE DATABASE controle_financeiro;
CREATE USER controle_user WITH PASSWORD 'troque_esta_senha';
GRANT ALL PRIVILEGES ON DATABASE controle_financeiro TO controle_user;
```

No PostgreSQL 15+, também é necessário (conectado já dentro do banco `controle_financeiro`):
```sql
GRANT ALL ON SCHEMA public TO controle_user;
```

Se você mudar a senha, atualize também em `src/main/resources/application.yml`.

## 3. Gerar a chave secreta do JWT

```bash
openssl rand -base64 32
```

Copie o resultado para `app.jwt.secret` em `application.yml`.

## 4. Rodar a aplicação

```bash
cd backend
mvn spring-boot:run
```

Na primeira execução, o Flyway roda automaticamente `V1__criar_tabelas.sql`.

## 5. Testar o cadastro e login

```bash
curl -X POST http://localhost:8080/auth/cadastro \
  -H "Content-Type: application/json" \
  -d '{"nome":"Seu Nome","email":"voce@exemplo.com","senha":"senha12345"}'
```

A resposta traz um `token`. Use-o em qualquer rota protegida:
```bash
curl http://localhost:8080/dashboard \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```
