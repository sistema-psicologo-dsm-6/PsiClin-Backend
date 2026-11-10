# Psi Clin Backend

## Identificação

- **Projeto:** Psi Clin (backend: `PsiClin-Backend`)
- **Grupo:** Agostinho Benigno Monteiro Gutierrez, Afonso Rocha Sprocatti, Eduarda Josileide da Silva, Lindalva Carlos da Silva, Westes Mesquita da Silva Oliveira
- **Repositório do backend:** https://github.com/sistema-psicologo-dsm-6/PsiClin-Backend
- **Repositório do frontend:** [https://github.com/sistema-psicologo-dsm-6/PsiClinFrontend](https://github.com/sistema-psicologo-dsm-6/PsiClin_Frontend.git)

## Integrantes e entidades sob responsabilidade

| Integrante (GitHub) | Nomes dos usuários | Entidades |
| --- | --- | --- |
| agostinho58| Agostinho Benigno Monteiro Gutierrez | Agenda, BloqueioAgenda |
| Dalvinha75 |  Lindalva Carlos da Silva | Prontuario, SalaAtendimento |
| Eduardajs3 | Eduarda Josileide da Silva | Paciente, HorarioDisponivel, Pagamento |
| Hush9K |Afonso Rocha Sprocatti | Funcionario, Convenio, EvolucaoClinica |
| westes.mso | Westes Mesquita da Silva Oliveira | Psicologo, Recepcionista, TipoAtendimento, contribuições gerais (autenticação, configuração e documentação) |

## O que a aplicação faz

O Psi Clin é um sistema de gestão de clínicas de psicologia. Ele centraliza o cadastro de pacientes, psicólogos, funcionários e recepcionistas, a agenda de atendimentos (horários disponíveis, bloqueios, salas e tipos de atendimento), convênios, prontuários com evoluções clínicas e o controle de pagamentos.

Problemas que resolve e melhorias que traz:

- Substitui planilhas e agendas de papel por um cadastro único e consistente, reduzindo perda de informação e retrabalho.
- Evita conflitos de agenda (horários, salas e bloqueios) e facilita o agendamento pela recepção.
- Mantém o histórico clínico de cada paciente (prontuário e evoluções) organizado e acessível ao profissional.
- Controla pagamentos e convênios, dando visibilidade financeira.
- Protege dados sensíveis com autenticação JWT e acesso restrito por perfil (ADMIN, psicólogo, recepcionista etc.).

## Camadas da arquitetura

### Backend (Kotlin + Spring Boot), em `src/main/kotlin/com/edu/psiclinbe/`

| Camada | Pasta |
| --- | --- |
| Apresentação (API REST) | `controller/` |
| Transferência de dados e validação | `dto/` |
| Conversão DTO ↔ entidade | `mapper/` |
| Domínio (entidades JPA e enums) | `model/` |
| Persistência (Spring Data JPA) | `repository/` |
| Regras de negócio | `service/` |
| Segurança (JWT, roles) | `security/` |
| Configuração e recursos | `src/main/resources/` |


## Instalação, execução e acesso

Resumo (detalhes nas seções seguintes):

1. Instale o JDK 25 e escolha o banco: PostgreSQL no Supabase ou MariaDB local (seção "Banco de dados").
2. Copie `.env.example` para `.env`, selecione a conexão correspondente e preencha as credenciais e `JWT_SECRET` (seção "Configurando o ambiente").
3. Execute `.\gradlew.bat bootRun` (Linux/macOS: `./gradlew bootRun`).
4. Crie o primeiro usuário por `POST /auth/signup` e depois autentique-se em `POST /auth/signin`; a API fica em `http://localhost:8080` (ou na porta configurada).
5. Para o frontend, siga o README do repositório dele e aponte-o para a URL da API.

O projeto está em desenvolvimento com Kotlin e Spring Boot. A base de domínio, os DTOs com validação, os repositories JPA e os controllers REST estão organizados por recurso.

## Tecnologias

- Kotlin 2.3.21
- Spring Boot 4.1.1
- Spring Data JPA
- Spring Web MVC
- Spring Security e JWT
- Jakarta Bean Validation
- PostgreSQL (Supabase) ou MariaDB
- Gradle Wrapper
- Java 25

## Pré-requisitos

- JDK 25 configurado no ambiente
- Uma instância PostgreSQL no Supabase ou MariaDB acessível pela aplicação
- Credenciais e endereço do banco escolhido configurados no arquivo `.env`

## Banco de dados

A aplicação pode usar PostgreSQL no Supabase ou MariaDB local. Os dois drivers JDBC estão disponíveis. Para alternar, selecione a URL JDBC correspondente em `src/main/resources/application.yaml` e ajuste as variáveis do `.env` para o mesmo banco. Mantenha somente uma URL JDBC ativa.

### Supabase (PostgreSQL)

Em **Connect** no painel do Supabase, obtenha os dados de conexão. Se sua rede não tiver IPv6, use o **Session pooler**; o endpoint direto pode não estar acessível. Copie host, porta, usuário e banco exatamente como apresentados e mantenha SSL habilitado (`DB_SSLMODE=require`). O usuário do pooler costuma incluir o identificador do projeto.

### Iniciando o MariaDB local

Escolha uma das opções abaixo para preparar MariaDB/MySQL local. O exemplo Docker usa `localhost:3307`; a URL JDBC correspondente precisa estar ativa em `application.yaml`.

### Backend com Docker Compose

O Compose inicia a API em um container e usa o banco configurado no `.env` (por exemplo, o Supabase). O arquivo `.env.example` contém apenas valores de exemplo. Se ainda não houver um `.env`, crie-o e informe suas configurações locais:

```powershell
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
```

Em seguida, inicie os serviços na raiz do projeto:

```powershell
docker compose up --build
```

A API ficará disponível em `http://localhost:8080` (ou na porta definida por `SERVER_PORT`). Para parar o container, execute `docker compose down`. O banco permanece externo ao Compose, então os dados não são apagados ao parar o serviço.

### Docker

Com o Docker Desktop instalado e em execução, crie e inicie um container MariaDB. O comando publica a porta `3306` do container como `3307` no computador, cria o banco `psiclinbe` e mantém os dados em um volume:

```powershell
docker run --name psiclinbe-mariadb --restart unless-stopped -e MARIADB_ROOT_PASSWORD=change-me -e MARIADB_DATABASE=psiclinbe -p 3307:3306 -v psiclinbe-mariadb-data:/var/lib/mysql -d mariadb:11.4
```

Na primeira inicialização, aguarde o MariaDB terminar de subir. Para conferir os logs:

```powershell
docker logs psiclinbe-mariadb
```

Nas próximas vezes, inicie e pare o banco com:

```powershell
docker start psiclinbe-mariadb
docker stop psiclinbe-mariadb
```

Ao usar esse container, configure as variáveis MariaDB/MySQL mostradas na seção "Alternando entre Supabase e MariaDB". Se o container com esse nome já existir, use `docker start psiclinbe-mariadb` em vez de executar `docker run` novamente.

### Serviço instalado no Windows

Em um PowerShell, localize o nome do serviço MariaDB instalado:

```powershell
Get-Service *maria*
```

Inicie o serviço usando o nome exibido. Em uma instalação padrão, ele costuma ser `MariaDB`:

```powershell
Start-Service -Name MariaDB
```

Se quiser iniciar o serviço automaticamente com o Windows:

```powershell
Set-Service -Name MariaDB -StartupType Automatic
```

Se o serviço tiver outro nome, substitua `MariaDB` pelo valor da coluna `Name`. Caso a conexão local use a porta padrão `3306`, altere `DB_PORT=3307` para `DB_PORT=3306` no `.env`.

Com o serviço iniciado, crie o banco caso ele ainda não exista. No cliente MariaDB, execute:

```sql
CREATE DATABASE psiclinbe;
```

### Configurando o ambiente

Crie o arquivo `.env` a partir do modelo:

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

No Linux ou macOS:

```bash
cp .env.example .env
```

Edite `.env` e informe as credenciais do banco escolhido. O arquivo `.env` é local e não deve ser commitado. O `.env.example` é apenas um modelo com placeholders; substitua-os pelos seus valores e nunca coloque credenciais reais nele.

Para Supabase, use os dados do **Session pooler** quando precisar de acesso IPv4. Para MariaDB local, use `localhost`, a porta configurada no servidor e o banco `psiclinbe`.

As variáveis disponíveis são:

| Variável | Descrição | Valor padrão |
| --- | --- | --- |
| `DB_HOST` | Host do banco escolhido | `localhost` (MariaDB) |
| `DB_PORT` | Porta do banco escolhido | `3307` (MariaDB) ou a informada pelo Supabase |
| `DB_SSLMODE` | Modo SSL do PostgreSQL | `require` (Supabase) |
| `DB_NAME` | Nome do banco | `psiclinbe` (MariaDB) ou `postgres` (Supabase) |
| `DB_USERNAME` | Usuário do banco | `root` (MariaDB) ou o usuário informado pelo Supabase |
| `DB_PASSWORD` | Senha do banco | Defina no `.env`; não use uma senha real no `.env.example` |
| `SERVER_PORT` | Porta HTTP da aplicação | `8080` |
| `JWT_SECRET` | Chave secreta usada para assinar os tokens JWT (mínimo de 32 caracteres) | **Obrigatória, sem valor padrão** |

#### Gerando a `JWT_SECRET`

A aplicação não inicia sem `JWT_SECRET`. Gere uma chave aleatória e cole no `.env`:

PowerShell (Windows):

```powershell
$bytes = New-Object byte[] 48
   $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
   $rng.GetBytes($bytes)
   $rng.Dispose()
   [Convert]::ToBase64String($bytes)
  ```

Linux/macOS/Git Bash:

```bash
openssl rand -base64 48
```

Depois adicione ao `.env`:

```env
JWT_SECRET=<valor gerado>
```

Use uma chave diferente por ambiente, nunca commite o `.env` e, se a chave vazar, gere outra (todos os tokens existentes deixam de valer).

O Spring Boot importa automaticamente o arquivo `.env` por meio de `src/main/resources/application.yaml`:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
  datasource:
    # Deixe ativa apenas a URL do banco escolhido.
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:postgres}?sslmode=${DB_SSLMODE:require}
    # MariaDB local: comente a URL PostgreSQL acima e descomente a linha abaixo.
    # url: jdbc:mariadb://${DB_HOST:localhost}:${DB_PORT:3307}/${DB_NAME:psiclinbe}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}

jwt:
  secret: ${JWT_SECRET}
```

As variáveis definidas diretamente no ambiente do sistema têm precedência sobre as variáveis carregadas pelo `.env`. Se o arquivo não existir, os valores padrão definidos no `application.yaml` serão utilizados, exceto `JWT_SECRET`, que é obrigatória.

#### Alternando entre Supabase e MariaDB

1. Em `application.yaml`, deixe ativa somente a URL `jdbc:postgresql:` ou `jdbc:mariadb:` correspondente ao banco desejado.
2. No `.env`, ajuste `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` e `DB_PASSWORD` para esse mesmo banco. Para Supabase, configure também `DB_SSLMODE=require` e prefira os dados do Session pooler quando não houver IPv6.
3. Os dois drivers já estão declarados em `build.gradle.kts`; não é necessário trocar dependências ao alternar.

Exemplo de valores para MariaDB local:

```env
DB_HOST=localhost
DB_PORT=3307
DB_NAME=psiclinbe
DB_USERNAME=root
DB_PASSWORD=<senha-local>
```

Para Supabase, substitua esses valores pelos dados de conexão fornecidos pelo painel. Nunca compartilhe ou versione a senha do banco.

## Executando o projeto

No Windows:

```powershell
.\gradlew.bat bootRun
```

No Linux ou macOS:

```bash
./gradlew bootRun
```

A aplicação Spring Boot será iniciada na porta padrão `8080`. Antes de iniciá-la, confirme que o banco selecionado está acessível e que o `.env` contém as credenciais correspondentes à URL ativa no `application.yaml`.

## Comandos úteis

Compilar o código principal:

```powershell
.\gradlew.bat compileKotlin
```

Compilar também os testes:

```powershell
.\gradlew.bat compileTestKotlin
```

Executar os testes:

```powershell
.\gradlew.bat test
```

O teste de contexto (`contextLoads`) precisa conseguir conectar ao banco selecionado no `application.yaml` e configurado no `.env`.

## Estrutura do projeto

```text
.
├── src/
│   ├── main/
│   │   ├── kotlin/com/edu/psiclinbe/
│   │   │   ├── controller/  # Endpoints REST
│   │   │   ├── dto/         # Objetos de entrada e saída
│   │   │   ├── mapper/      # Conversão entre DTOs e entidades
│   │   │   ├── model/       # Entidades JPA e enums do domínio
│   │   │   ├── repository/  # Interfaces Spring Data JPA
│   │   │   ├── service/     # Regras de negócio
│   │   │   └── PsiclinbeApplication.kt
│   │   └── resources/
│   │       └── application.yaml
│   └── test/kotlin/         # Testes automatizados
├── .env.example             # Modelo de configuração versionado
├── .gitignore
├── gradlew
└── gradlew.bat
```

## Domínio implementado

As entidades atuais representam:

- Pacientes
- Funcionários (este represeta o admin), psicólogos e recepcionistas
- Agendas e bloqueios de agenda
- Horários disponíveis
- Tipos de atendimento
- Salas de atendimento
- Convênios
- Prontuários e evoluções clínicas
- Pagamentos

Cada entidade possui um repository correspondente em `repository/`, baseado em `JpaRepository`, com consultas derivadas para filtros por nome, status, relacionamento e período quando aplicável.

## DTOs e validações

Os DTOs ficam no pacote `com.edu.psiclinbe.dto`. Os relacionamentos são recebidos como IDs, evitando o acoplamento direto entre payloads e entidades JPA.

As validações usam Jakarta Bean Validation, incluindo:

- `@NotBlank` para textos obrigatórios
- `@NotNull` para datas, IDs, enums e valores obrigatórios
- `@Email` para endereços de e-mail
- `@Size` para limites de texto
- `@Positive` e `@PositiveOrZero` para valores numéricos

Os controllers usam os DTOs com `@Valid` nos parâmetros de entrada para que essas validações sejam executadas automaticamente.

## Configuração JPA

O Hibernate está configurado para atualizar o schema automaticamente durante o desenvolvimento:

```yaml
spring:
	jpa:
		hibernate:
			ddl-auto: update
```

O SQL também está habilitado para facilitar o desenvolvimento e a depuração. Em produção, recomenda-se utilizar migrações versionadas e revisar essas configurações.

## Autenticação e perfis (roles)

A API usa Spring Security com JWT. O login é feito por e-mail e senha de um funcionário:

```http
POST /auth/signin
Content-Type: application/json

{ "email": "admin@psiclin.local", "senha": "Admin123!" }
```

A resposta é `{ "token": "..." }`. Envie o token em todas as demais requisições: `Authorization: Bearer <token>`. Sem token ou com token inválido a API responde `401`; com um perfil sem permissão, `403`.

| Rota | Perfis com acesso |
| --- | --- |
| `/auth/signin` | Público |
| `/auth/signup` | Público somente enquanto não houver funcionários cadastrados; cria o primeiro usuário como ADMIN |
| `/funcionarios/**` | ADMIN |
| `/psicologos/**` | GET: ADMIN, RECEPCIONISTA, PSICOLOGO; escrita: ADMIN |
| `/recepcionistas/**` | GET: ADMIN, RECEPCIONISTA; escrita: ADMIN |
| `/prontuarios/**`, `/evolucoes-clinicas/**` | ADMIN, PSICOLOGO |
| `/horarios-disponiveis/**`, `/bloqueios-agenda/**` | GET: todos os perfis; escrita: ADMIN, PSICOLOGO |
| `/pagamentos/**` | ADMIN, RECEPCIONISTA |
| `/agendas/**`, `/pacientes/**`, `/convenios/**`, `/salas-atendimento/**`, `/tipos-atendimento/**` | GET: todos os perfis; escrita: ADMIN, RECEPCIONISTA |

Qualquer outra rota é negada.

### Criando o primeiro ADMIN

Com o banco vazio de funcionários, crie a conta inicial chamando o endpoint público abaixo. O servidor atribui a role `ADMIN`, independentemente do valor de `role` enviado no JSON. Depois desse cadastro, novas chamadas públicas a `/auth/signup` são negadas; a criação e gestão dos próximos funcionários exigem autenticação como ADMIN pela rota `/funcionarios/**`.

```http
POST http://localhost:8080/auth/signup
Content-Type: application/json

{
  "nome": "Administrador",
  "cpf": "00000000000",
  "email": "admin@psiclin.local",
  "telefone": "11999999999",
  "senha": "TroqueEstaSenha123!"
}
```

### Testando a API

- `docs/api-tests.http`: coleção para a extensão REST Client (usa porta 8081, faz login como ADMIN e inclui casos 401/403). Ajuste `@adminEmail` e `@adminSenha` no topo.
- `docs/api-test-requests.json`: lista de todas as requisições, com os perfis exigidos.

## Segurança

- Nunca commit as credenciais reais no `.env`, no `application.yaml` ou em outros arquivos versionados.
- Use senhas específicas para cada ambiente.
- Em produção, prefira variáveis de ambiente gerenciadas pela infraestrutura ou um serviço de secrets.
