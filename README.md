# 🏆 SportPro — Sistema de Gerenciamento Esportivo

> **TCC — Sistemas de Informação**
> Plataforma web para conectar atletas e treinadores, com geração de cronogramas personalizados via integração n8n.

---

## 📋 Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Estrutura de Diretórios](#estrutura-de-diretórios)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Integração n8n](#integração-n8n)
- [Exemplos JSON](#exemplos-json)
- [Segurança](#segurança)
- [Fluxo Completo do Sistema](#fluxo-completo-do-sistema)

---

## 🎯 Visão Geral

O **SportPro** é uma plataforma web que conecta **atletas** e **treinadores** esportivos. O sistema permite:

- **Treinadores** cadastrarem modalidades, metodologias e gerenciarem seus atletas
- **Atletas** escolherem treinadores, enviarem perfil esportivo e receberem cronogramas personalizados
- **Integração com n8n** para processamento inteligente e geração de cronogramas via IA

---

## 🛠 Tecnologias

| Camada      | Tecnologia                                |
|-------------|-------------------------------------------|
| Backend     | Java 21, Spring Boot 3.2, Maven           |
| Persistência| Spring Data JPA, Hibernate, MySQL 8       |
| Segurança   | Spring Security, BCrypt                   |
| Frontend    | HTML5, CSS3, JavaScript puro (Fetch API)  |
| Automação   | n8n (webhook HTTP)                        |
| Extras      | Lombok, Bean Validation, CORS             |

---

## 🏛 Arquitetura

O backend segue arquitetura em camadas (layered architecture):

```
Request HTTP
     │
     ▼
┌─────────────┐
│  Controller  │  ← Recebe requisição, valida DTO, delega ao Service
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │  ← Lógica de negócio, transações, mapeamento DTO↔Entity
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ Repository  │  ← Acesso ao banco via Spring Data JPA
└──────┬──────┘
       │
       ▼
┌─────────────┐
│    MySQL    │  ← Persistência dos dados
└─────────────┘
```

**Princípios aplicados:**
- **SOLID**: cada classe tem responsabilidade única
- **DTO Pattern**: entidades JPA nunca expostas na API
- **Exception Handler Global**: erros centralizados
- **Transações declarativas**: `@Transactional` nos services
- **Clean Code**: nomes expressivos, comentários relevantes

---

## 📁 Estrutura de Diretórios

```
sportpro/
│
├── backend/                              # Projeto Spring Boot (Maven)
│   ├── pom.xml                           # Dependências Maven
│   └── src/main/
│       ├── java/com/sportpro/
│       │   ├── SportProApplication.java  # Ponto de entrada da aplicação
│       │   │
│       │   ├── controller/               # Camada de apresentação (REST)
│       │   │   ├── AuthController.java       # POST /api/auth/login
│       │   │   ├── TreinadorController.java  # CRUD de treinadores
│       │   │   ├── AtletaController.java     # CRUD de atletas + cronograma
│       │   │   ├── ModalidadeController.java # CRUD de modalidades
│       │   │   └── MetodologiaController.java# CRUD de metodologias
│       │   │
│       │   ├── service/                  # Camada de negócio
│       │   │   ├── AuthService.java          # Autenticação (email + BCrypt)
│       │   │   ├── TreinadorService.java     # Regras de negócio do treinador
│       │   │   ├── AtletaService.java        # Regras de negócio do atleta
│       │   │   ├── ModalidadeService.java    # Regras de modalidades
│       │   │   ├── MetodologiaService.java   # Regras de metodologias
│       │   │   └── CronogramaService.java    # Orquestra geração via n8n
│       │   │
│       │   ├── repository/               # Acesso a dados (Spring Data JPA)
│       │   │   ├── TreinadorRepository.java
│       │   │   ├── AtletaRepository.java
│       │   │   ├── ModalidadeRepository.java
│       │   │   ├── MetodologiaRepository.java
│       │   │   └── CronogramaRepository.java
│       │   │
│       │   ├── entity/                   # Entidades JPA (tabelas do banco)
│       │   │   ├── Treinador.java
│       │   │   ├── Atleta.java
│       │   │   ├── Modalidade.java
│       │   │   ├── Metodologia.java
│       │   │   └── Cronograma.java
│       │   │
│       │   ├── dto/                      # Objetos de transferência de dados
│       │   │   ├── request/              # DTOs de entrada (POST/PUT)
│       │   │   │   ├── TreinadorRequest.java
│       │   │   │   ├── AtletaRequest.java
│       │   │   │   ├── PerfilEsportivoRequest.java
│       │   │   │   ├── ModalidadeRequest.java
│       │   │   │   ├── MetodologiaRequest.java
│       │   │   │   └── LoginRequest.java
│       │   │   └── response/             # DTOs de saída (GET)
│       │   │       ├── ApiResponseDto.java   # Wrapper genérico { success, message, data }
│       │   │       ├── TreinadorResponseDto.java
│       │   │       ├── AtletaResponseDto.java
│       │   │       ├── ModalidadeResponseDto.java
│       │   │       ├── MetodologiaResponseDto.java
│       │   │       ├── CronogramaResponseDto.java
│       │   │       └── LoginResponseDto.java
│       │   │
│       │   ├── config/                   # Configurações Spring
│       │   │   └── SecurityConfig.java       # BCrypt, CORS, HTTP Security
│       │   │
│       │   ├── exception/               # Tratamento de erros
│       │   │   ├── ResourceNotFoundException.java  # 404
│       │   │   ├── BusinessException.java          # 400
│       │   │   └── GlobalExceptionHandler.java     # @ControllerAdvice
│       │   │
│       │   └── integration/             # Integração externa
│       │       └── N8nWebhookIntegration.java  # HTTP client para n8n
│       │
│       └── resources/
│           └── application.properties    # Configurações do Spring Boot
│
├── frontend/                             # Interface web (HTML/CSS/JS puro)
│   ├── css/
│   │   └── global.css                    # Design system completo (variáveis, componentes)
│   ├── js/
│   │   └── api.js                        # Módulo de comunicação com a API + utilitários
│   └── pages/
│       ├── index.html                    # Landing page
│       ├── login.html                    # Tela de login (atleta/treinador)
│       ├── cadastro-atleta.html          # Cadastro de atleta (RF004)
│       ├── cadastro-treinador.html       # Cadastro de treinador (RF001)
│       ├── dashboard-treinador.html      # Dashboard completo do treinador
│       └── dashboard-atleta.html         # Dashboard completo do atleta
│
├── sportpro.sql                          # Script SQL completo (schema + seed)
└── README.md                             # Esta documentação
```

---

## ✅ Pré-requisitos

Antes de executar, instale:

| Ferramenta | Versão mínima | Download |
|------------|---------------|----------|
| Java JDK   | 21            | https://adoptium.net |
| Maven      | 3.9+          | https://maven.apache.org |
| MySQL      | 8.0+          | https://dev.mysql.com/downloads |
| n8n (opcional) | qualquer  | https://n8n.io |

---

## 🚀 Como Executar

### 1. Banco de Dados (MySQL)

```bash
# Acesse o MySQL
mysql -u root -p

# Execute o script SQL (cria banco + tabelas + dados de exemplo)
source /caminho/para/sportpro/sportpro.sql;

# Ou via linha de comando:
mysql -u root -p < sportpro.sql
```

### 2. Backend (Spring Boot)

```bash
# Entre na pasta do backend
cd sportpro/backend

# Edite as credenciais do MySQL em:
# src/main/resources/application.properties
# spring.datasource.username=SEU_USUARIO
# spring.datasource.password=SUA_SENHA

# Compile e execute
mvn spring-boot:run

# Ou gere o JAR e execute:
mvn clean package -DskipTests
java -jar target/sportpro-backend-1.0.0.jar
```

O backend iniciará em: **http://localhost:8080**

### 3. Frontend

O frontend é **HTML/CSS/JS puro**, sem necessidade de build.

**Opção A — VS Code Live Server (recomendado):**
```
1. Instale a extensão "Live Server" no VS Code
2. Clique com botão direito em frontend/pages/index.html
3. Selecione "Open with Live Server"
```

**Opção B — Python HTTP Server:**
```bash
cd sportpro/frontend
python3 -m http.server 3000
# Acesse: http://localhost:3000/pages/index.html
```

**Opção C — Abrir diretamente no browser:**
```
Abra o arquivo frontend/pages/index.html diretamente no browser.
Nota: alguns browsers bloqueiam fetch() com file:// — use Live Server neste caso.
```

### 4. Credenciais de exemplo (após rodar o SQL seed)

| Tipo      | Email                  | Senha     |
|-----------|------------------------|-----------|
| Treinador | carlos@sportpro.com    | senha123  |
| Treinador | ana@sportpro.com       | senha123  |
| Atleta    | pedro@sportpro.com     | senha123  |

---

## 🌐 Endpoints da API

### Autenticação
| Método | Endpoint         | Descrição           |
|--------|------------------|---------------------|
| POST   | `/api/auth/login`| Login (atleta/treinador) |

### Treinadores
| Método | Endpoint                | Descrição                    |
|--------|-------------------------|------------------------------|
| POST   | `/api/treinadores`      | Cadastrar treinador (RF001)  |
| GET    | `/api/treinadores`      | Listar todos os treinadores  |
| GET    | `/api/treinadores/{id}` | Buscar treinador por ID      |

### Atletas
| Método | Endpoint                           | Descrição                         |
|--------|------------------------------------|-----------------------------------|
| POST   | `/api/atletas`                     | Cadastrar atleta (RF004)          |
| GET    | `/api/atletas`                     | Listar todos os atletas           |
| GET    | `/api/atletas/{id}`                | Buscar atleta por ID              |
| PUT    | `/api/atletas/perfil`              | Enviar perfil esportivo (RF007)   |
| POST   | `/api/atletas/{id}/cronograma`     | Gerar cronograma via n8n (RF008)  |
| GET    | `/api/atletas/{id}/cronogramas`    | Histórico de cronogramas          |
| GET    | `/api/atletas/treinador/{id}`      | Atletas por treinador             |

### Modalidades
| Método | Endpoint                              | Descrição                      |
|--------|---------------------------------------|--------------------------------|
| POST   | `/api/modalidades`                    | Cadastrar modalidade (RF002)   |
| GET    | `/api/modalidades`                    | Listar todas                   |
| GET    | `/api/modalidades/treinador/{id}`     | Modalidades por treinador      |

### Metodologias
| Método | Endpoint                               | Descrição                     |
|--------|----------------------------------------|-------------------------------|
| POST   | `/api/metodologias`                    | Cadastrar metodologia (RF003) |
| GET    | `/api/metodologias/treinador/{id}`     | Metodologias por treinador    |

---

## 🔗 Integração n8n

### Como funciona

```
1. Atleta clica em "Gerar Cronograma" no frontend
2. Frontend → POST /api/atletas/{id}/cronograma
3. Backend (CronogramaService) monta payload JSON com dados do atleta, treinador e metodologia
4. Backend → POST http://localhost:5678/webhook/sportpro (n8n)
5. n8n processa os dados (pode usar IA como OpenAI, Claude, etc.)
6. n8n retorna JSON: { treinoSemanal, dieta, observacoes }
7. Backend salva o Cronograma no banco
8. Backend retorna o cronograma ao frontend
9. Frontend exibe o cronograma para o atleta
```

### Configurar o n8n

```bash
# Instalar n8n globalmente
npm install -g n8n

# Executar
n8n start

# Acesse: http://localhost:5678
```

### Criar o Workflow no n8n

1. Acesse http://localhost:5678
2. Crie um novo workflow
3. Adicione o nó **"Webhook"** com:
   - HTTP Method: POST
   - Path: `sportpro`
4. Adicione lógica de processamento (código, IA, etc.)
5. Retorne um JSON com a estrutura abaixo
6. Ative o workflow

### Alterar a URL do webhook

Em `backend/src/main/resources/application.properties`:
```properties
n8n.webhook.url=http://localhost:5678/webhook/sportpro
```

### Modo sem n8n (desenvolvimento)

Se o n8n não estiver disponível, o sistema usa um **cronograma simulado** automaticamente. Você pode testar todas as funcionalidades sem configurar o n8n.

---

## 📄 Exemplos JSON

### POST /api/auth/login
```json
{
  "email": "carlos@sportpro.com",
  "senha": "senha123",
  "tipo": "TREINADOR"
}
```

**Resposta:**
```json
{
  "success": true,
  "message": "Login realizado com sucesso!",
  "data": {
    "id": 1,
    "nome": "Carlos Mendes",
    "email": "carlos@sportpro.com",
    "tipo": "TREINADOR",
    "mensagem": "Login realizado com sucesso!"
  }
}
```

### POST /api/atletas
```json
{
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "senha": "minhasenha123",
  "idade": 25,
  "peso": 58.5,
  "altura": 1.65
}
```

### PUT /api/atletas/perfil
```json
{
  "atletaId": 2,
  "treinadorId": 1,
  "modalidadeId": 1,
  "objetivo": "Correr os 100m em menos de 12 segundos em 4 meses",
  "experiencia": "INICIANTE",
  "diasDisponiveis": 4,
  "limitacoesFisicas": "Leve dor no joelho direito após impacto",
  "observacoes": "Prefiro treinar pela manhã"
}
```

### Payload enviado ao n8n
```json
{
  "atleta": {
    "id": 2,
    "nome": "Maria Silva",
    "idade": 25,
    "peso": 58.5,
    "altura": 1.65,
    "objetivo": "Correr os 100m em menos de 12 segundos",
    "experiencia": "INICIANTE",
    "diasDisponiveis": 4,
    "limitacoesFisicas": "Leve dor no joelho direito após impacto",
    "imc": 21.5
  },
  "treinador": {
    "id": 1,
    "nome": "Carlos Mendes"
  },
  "metodologia": {
    "titulo": "Periodização Linear para Velocistas",
    "descricao": "...",
    "estrategias": "...",
    "recomendacoesAlimentares": "...",
    "criteriosEvolucao": "..."
  },
  "modalidade": {
    "id": 1,
    "nome": "100m rasos"
  }
}
```

### Retorno esperado do n8n
```json
{
  "treinoSemanal": "=== CRONOGRAMA SEMANAL ===\n\nSegunda: Corrida leve 20 min...",
  "dieta": "=== PLANO ALIMENTAR ===\n\nPré-treino: carboidrato...",
  "observacoes": "Atenção especial ao joelho. Evitar impacto excessivo na primeira semana..."
}
```

---

## 🔒 Segurança

| Recurso              | Implementação                                      |
|----------------------|----------------------------------------------------|
| Senha                | Hash BCrypt (Spring Security, fator 10)            |
| CORS                 | Configurado em `SecurityConfig.java`               |
| Validações           | Bean Validation (`@NotBlank`, `@Email`, `@Min`...) |
| Erros padronizados   | `GlobalExceptionHandler` (nunca stack trace)       |
| JWT (futuro)         | Estrutura preparada no `AuthService`               |

**Para adicionar JWT:**
1. Adicione dependência `jjwt` no pom.xml
2. Crie `JwtService` para gerar/validar tokens
3. Crie `JwtFilter extends OncePerRequestFilter`
4. Registre o filtro em `SecurityConfig`
5. Retorne o token no `LoginResponseDto`

---

## 🔄 Fluxo Completo do Sistema

```
TREINADOR                    ATLETA                      n8n
   │                           │                           │
   │ 1. Cadastro               │                           │
   │──────────────────────────▶│                           │
   │                           │                           │
   │ 2. Cadastra modalidades   │                           │
   │                           │                           │
   │ 3. Define metodologia     │                           │
   │                           │                           │
   │                    4. Atleta se cadastra              │
   │                           │                           │
   │                    5. Escolhe modalidade              │
   │                           │                           │
   │                    6. Seleciona treinador             │
   │                           │                           │
   │                    7. Preenche perfil                 │
   │                    (objetivo, experiência, dias)      │
   │                           │                           │
   │                    8. Solicita cronograma             │
   │                           │── POST webhook ──────────▶│
   │                           │                    Processa│
   │                           │◀─── JSON cronograma ──────│
   │                           │                           │
   │                    9. Visualiza cronograma            │
   │                           │                           │
   │ 10. Acompanha atletas     │                           │
   │◀──────────────────────────│                           │
```

---

## 🎓 Informações do TCC

- **Curso:** Sistemas de Informação
- **Disciplina:** Trabalho de Conclusão de Curso
- **Tecnologias:** Java 21, Spring Boot, MySQL, HTML/CSS/JS, n8n
- **Arquitetura:** REST API + SPA frontend + Webhooks

---

*SportPro — Desenvolvido como projeto acadêmico de TCC em Sistemas de Informação.*
