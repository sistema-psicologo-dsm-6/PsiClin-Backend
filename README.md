# Psi Clin Backend

Backend do Psi Clin, sistema de apoio à gestão de atendimentos psicológicos.

O projeto está em desenvolvimento com Kotlin e Spring Boot. A base de domínio, os DTOs com validação, os repositories JPA e os controllers REST estão organizados por recurso. Os contratos dos services seguem em evolução.

## Tecnologias

- Kotlin 2.3.21
- Spring Boot 4.1.1
- Spring Data JPA
- Spring Web MVC
- Jakarta Bean Validation
- MariaDB
- Gradle Wrapper
- Java 25

## Pré-requisitos

- JDK 25 configurado no ambiente
- MariaDB em execução e acessível pela aplicação
- Banco de dados `psiclinbe` criado (ou outro nome definido em `DB_NAME` no arquivo `.env`)

## Iniciando o MariaDB

Escolha uma das opções abaixo. A aplicação está configurada por padrão para conectar em `localhost:3307`.

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

O `.env.example` já corresponde a essa configuração. Se o container com esse nome já existir, use `docker start psiclinbe-mariadb` em vez de executar `docker run` novamente.

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

Edite `.env` e informe as credenciais do seu MariaDB. O arquivo `.env` é local e não deve ser commitado. O arquivo `.env.example` contém apenas a estrutura necessária para configurar o ambiente.

As variáveis disponíveis são:

| Variável | Descrição | Valor padrão |
| --- | --- | --- |
| `DB_HOST` | Host do MariaDB | `localhost` |
| `DB_PORT` | Porta do MariaDB | `3307` |
| `DB_NAME` | Nome do banco de dados | `psiclinbe` |
| `DB_USERNAME` | Usuário do banco | `root` |
| `DB_PASSWORD` | Senha do banco | `root` (o `.env.example` usa `change-me`) |
| `SERVER_PORT` | Porta HTTP da aplicação | `8080` |

O Spring Boot importa automaticamente o arquivo `.env` por meio de `src/main/resources/application.yaml`:

```yaml
spring:
  config:
    import: optional:file:.env[.properties]
  datasource:
    url: jdbc:mariadb://${DB_HOST:localhost}:${DB_PORT:3307}/${DB_NAME:psiclinbe}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
```

As variáveis definidas diretamente no ambiente do sistema têm precedência sobre as variáveis carregadas pelo `.env`. Se o arquivo não existir, os valores padrão definidos no `application.yaml` serão utilizados.

## Executando o projeto

No Windows:

```powershell
.\gradlew.bat bootRun
```

No Linux ou macOS:

```bash
./gradlew bootRun
```

A aplicação Spring Boot será iniciada na porta padrão `8080`. Antes de iniciá-la, confirme que o MariaDB está ativo e que o `.env` contém as credenciais correspondentes ao banco.

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

O teste de contexto (`contextLoads`) precisa conseguir conectar ao MariaDB configurado no `application.yaml`.

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
- Funcionários, psicólogos e recepcionistas
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

## Segurança

- Nunca commit as credenciais reais no `.env`, no `application.yaml` ou em outros arquivos versionados.
- Use senhas específicas para cada ambiente.
- Em produção, prefira variáveis de ambiente gerenciadas pela infraestrutura ou um serviço de secrets.