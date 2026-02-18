# API de Gestão Paroquial

API REST completa para gestão de paróquias católicas desenvolvida em **Java 21** com **Spring Boot 4.0.2**.

## 🚀 Funcionalidades Implementadas

### ✅ Core (Fase 1)
- **Autenticação JWT** com access token + refresh token
- **Bootstrap Admin** - Criação do usuário administrador inicial
- **Login/Logout** - Autenticação completa
- **Refresh Token** - Renovação de access token
- **Change Password** - Alteração de senha do usuário logado
- **RBAC** - Controle de acesso baseado em roles (SUPER_ADMIN, SECRETARIO, CHEFE_NUCLEO)
- **14 Entidades JPA** - Modelo de dados completo
- **15 Repositories** - Acesso a dados com Spring Data JPA
- **Global Exception Handler** - Tratamento padronizado de erros
- **OpenAPI/Swagger** - Documentação interativa da API
- **CORS** - Configurado para a UI

### 📦 Stack Tecnológica

| Tecnologia | Versão | Uso |
|------------|--------|-----|
| Java | 21 | Linguagem |
| Spring Boot | 4.0.2 | Framework |
| PostgreSQL | 16+ | Banco de Dados |
| Maven | 3.8+ | Build Tool |
| ULID Creator | 8.3.0 | Geração de IDs |
| JJWT | 0.12.6 | JWT |
| Springdoc OpenAPI | 2.8.3 | Swagger |
| Flying Saucer | 9.11.1 | Geração de PDFs |
| Testcontainers | 1.20.4 | Testes |

---

## 🏗️ Estrutura do Projeto

```
TCC_API/
├── src/main/java/com/vaticano/paroquia/
│   ├── config/              # Configurações (Security, CORS, OpenAPI)
│   ├── controller/          # REST Controllers
│   ├── domain/
│   │   ├── entity/          # Entidades JPA (14 entidades)
│   │   ├── repository/      # Repositories (15 interfaces)
│   │   └── enums/           # Enums (9 tipos)
│   ├── dto/
│   │   ├── request/         # DTOs de entrada
│   │   └── response/        # DTOs de saída
│   ├── exception/           # Exceções customizadas + GlobalExceptionHandler
│   ├── security/
│   │   ├── jwt/             # JwtService + JwtAuthenticationFilter
│   │   └── SecurityUtils    # Utilitários de segurança
│   ├── service/             # Services (lógica de negócio)
│   ├── util/                # Utilitários (ULID, Normalize, MemberKey)
│   └── ParoquiaApplication.java
├── src/main/resources/
│   └── application.yaml     # Configurações (dev, prod, test)
├── docker-compose.yml       # PostgreSQL + pgAdmin
├── pom.xml                  # Dependências Maven
└── README.md
```

---

## 🎯 Setup Local

### 1. Pré-requisitos

- **Java 21** ou superior ([Download](https://adoptium.net/))
- **Maven 3.8+** ([Download](https://maven.apache.org/download.cgi))
- **Docker** e **Docker Compose** ([Download](https://www.docker.com/products/docker-desktop))

Verificar instalações:
```bash
java -version
mvn -version
docker --version
docker-compose --version
```

### 2. Clonar Repositório

```bash
git clone <repository-url>
cd TCC_API
```

### 3. Iniciar PostgreSQL com Docker

```bash
docker-compose up -d postgres
```

Isso inicia PostgreSQL em `localhost:5432` com:
- **Database:** `paroquia_dev`
- **User:** `postgres`
- **Password:** `postgres`

Para verificar o status:
```bash
docker-compose ps
```

Para parar:
```bash
docker-compose down
```

### 4. Compilar o Projeto

```bash
mvn clean install
```

### 5. Executar a Aplicação

```bash
mvn spring-boot:run
```

A API estará disponível em: **http://localhost:8080**

---

## 📋 Primeiros Passos

### 1. Criar Administrador Inicial

Faça uma requisição POST para criar o primeiro usuário admin:

```bash
curl -X POST http://localhost:8080/api/auth/bootstrap
```

**Resposta:**
```json
{
  "timestamp": "2026-02-15T14:30:00",
  "message": "Usuário administrador criado com sucesso. Username: admin, Password: admin123"
}
```

### 2. Fazer Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Resposta:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "rt_01hqxyz...",
  "tokenType": "Bearer",
  "user": {
    "userId": "usr_01hqxyz...",
    "username": "admin",
    "name": "Administrador",
    "role": "super_admin",
    "active": true
  }
}
```

### 3. Usar Access Token

Para endpoints protegidos, inclua o header Authorization:

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer {accessToken}"
```

### 4. Renovar Access Token

Quando o access token expirar (15 minutos), use o refresh token:

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "rt_01hqxyz..."
  }'
```

---

## 🌐 Documentação Swagger

Acesse a documentação interativa em:

**http://localhost:8080/swagger-ui.html**

Lá você pode:
- Ver todos os endpoints disponíveis
- Testar requisições diretamente no navegador
- Ver schemas de DTOs
- Autenticar usando o botão "Authorize" (cole o access token)

---

## 🔐 Endpoints de Autenticação

| Método | Endpoint | Descrição | Auth |
|--------|----------|-----------|------|
| POST | `/api/auth/bootstrap` | Criar admin inicial | Público |
| POST | `/api/auth/login` | Login | Público |
| POST | `/api/auth/refresh` | Renovar access token | Público |
| POST | `/api/auth/logout` | Logout | Público |
| PUT | `/api/auth/me/password` | Alterar senha | JWT |

---

## 🗂️ Modelo de Dados

### Entidades Implementadas (14)

1. **User** - Usuários do sistema (super_admin, secretario, chefe_nucleo)
2. **RefreshToken** - Tokens de refresh para autenticação
3. **Member** - Membros da paróquia (27 campos do CSV)
4. **Family** - Famílias
5. **FamilyMemberLink** - Vínculo membro-família
6. **Nucleo** - Núcleos/comunidades
7. **Activity** - Atividades dos núcleos
8. **Cargo** - Cargos por núcleo
9. **Contribuicao** - Contribuições financeiras
10. **ImagemActividade** - Imagens de atividades
11. **VisitaFamiliar** - Visitas familiares
12. **CertificateRequest** - Solicitações de certificados
13. **WhatsappConfig** - Configuração WhatsApp
14. **WhatsappNotificacao** - Notificações WhatsApp
15. **AuditLog** - Log de auditoria

### Enums Implementados (9)

- `Role` - Papéis de usuário
- `EstadoActividade` - Estados de atividade
- `TipoContribuicao` - Tipos de contribuição
- `MetodoPagamento` - Métodos de pagamento
- `TipoCertificado` - Tipos de certificado
- `EstadoCertificado` - Estados de certificado
- `EstadoVisita` - Estados de visita
- `EstadoCargo` - Estados de cargo
- `TriggerNotificacao` - Gatilhos de notificação

---

## 🛠️ Desenvolvimento

### Profile DEV (Padrão)

- Auto-criação de tabelas (JPA DDL: update)
- Logs detalhados (DEBUG)
- PostgreSQL local (localhost:5432)

### Alterar Profile

Edite `src/main/resources/application.yaml`:
```yaml
spring:
  profiles:
    active: prod  # Altere para prod ou test
```

Ou via variável de ambiente:
```bash
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

---

## 🧪 Testes

### Executar Testes

```bash
mvn test
```

Os testes usam **Testcontainers** para criar um PostgreSQL temporário.

---

## 📊 pgAdmin (Opcional)

Para visualizar o banco de dados graficamente:

```bash
docker-compose up -d pgadmin
```

Acesse: **http://localhost:5050**

- **Email:** `admin@paroquia.local`
- **Password:** `admin`

Adicionar servidor:
- **Host:** `postgres` (nome do container)
- **Port:** `5432`
- **Username:** `postgres`
- **Password:** `postgres`
- **Database:** `paroquia_dev`

---

## 🔒 Segurança

### Autenticação
- **JWT HS512** com secret de 256 bits
- **Access Token:** Expira em 15 minutos
- **Refresh Token:** Expira em 7 dias, armazenado em PostgreSQL

### Password Hashing
- **SHA-512** com salt aleatório de 64 bytes
- Salt único por usuário

### CORS
- Configurado em `application.yaml`
- Permite `http://localhost:5173` (UI) em desenvolvimento

---

## 🚨 Troubleshooting

### Erro: "Port 5432 already in use"
PostgreSQL já está rodando localmente. Pare o serviço ou altere a porta no `docker-compose.yml`:
```yaml
ports:
  - "5433:5432"
```

### Erro: "Could not create connection to database server"
Verifique se o PostgreSQL está rodando:
```bash
docker-compose ps
```

### Erro: "JWT secret too short"
O secret JWT deve ter pelo menos 256 bits (32 caracteres). Verifique `application.yaml`:
```yaml
app:
  jwt:
    secret: change-this-secret-in-production-must-be-at-least-256-bits-long-for-hs512
```

### Compilação falha
Limpe o cache do Maven:
```bash
mvn clean
mvn install
```

---

## 📈 Próximos Passos (Fase 2+)

- [ ] CRUD de Membros com importação CSV
- [ ] CRUD de Famílias e vínculos
- [ ] CRUD de Núcleos
- [ ] CRUD de Atividades com upload de imagens
- [ ] CRUD de Contribuições com comprovativos
- [ ] Geração de Certificados (Batismo, Crisma, Casamento)
- [ ] Integração WhatsApp
- [ ] Dashboard com estatísticas

---

## 📝 Convenções de Código

### IDs
- Formato: `prefixo_ulid` (ex: `usr_01hqxyz...`, `mbr_01hqabc...`)
- Prefixos: usr_, mbr_, fam_, nucleo_, act_, cargo_, cont_, visita_, certreq_, rt_, img_, wanotif_, audit_

### Datas
- Datas do CSV: **String** (não LocalDate)
- Datas do sistema: **LocalDate** ou **LocalDateTime**

### Soft Delete
- Campos: `deletedAt`, `deletedBy`
- Anotação: `@Where(clause="deleted_at IS NULL")`

---

## 📞 Suporte

Para dúvidas ou problemas:
- **Email:** admin@paroquia.local
- **Issues:** [GitHub Issues](<repository-url>/issues)

---

## 📄 Licença

Propriedade da Vaticano Paróquia. Todos os direitos reservados.

---

**🎉 API pronta para uso! Execute `mvn spring-boot:run` e acesse http://localhost:8080/swagger-ui.html para começar.**
