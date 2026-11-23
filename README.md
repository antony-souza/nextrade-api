# Nextrade API

API modular em Java 17 e Spring Boot 4.1.1, seguindo a estrutura do OpenJobs. Os módulos `auth`, `users`, `roles`, `permissions` e `rolepermissions` organizam controllers, casos de uso, modelos e repositórios.

## Executar localmente

Você vai precisar de Docker com Compose e de um JDK 17 ou superior. No Windows, configure `JAVA_HOME` se o Java estiver fora do `PATH`.

1. Copie `.env.example` para `.env` e preencha `POSTGRES_PASSWORD` e `JWT_KEY`. A chave JWT deve ser uma sequência aleatória de pelo menos 32 bytes codificada em Base64. No PowerShell, use `[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))`.
2. Para criar o administrador no primeiro start, defina `ADMIN_SEED_ENABLED=true`, `ADMIN_SEED_EMAIL` e `ADMIN_SEED_PASSWORD`. O valor padrão de `ADMIN_SEED_USERNAME` é `admin`. Após a criação da conta, você pode desabilitar a seed e remover a senha do `.env`.
3. Execute `docker compose up -d --wait postgres` e depois `.\mvnw.cmd spring-boot:run` no Windows ou `./mvnw spring-boot:run` no Linux/macOS.

Ao executar o JAR a partir da pasta pai do projeto, a API também procura o arquivo em `nextrade-api/.env`.

A API fica em `http://localhost:8082/api`. As origens do frontend podem ser configuradas em `CORS_ALLOWED_ORIGINS`.

## Autenticação e permissões

O Flyway cria as tabelas, o catálogo de permissões e os vínculos iniciais. A role `admin` recebe todas as permissões base. A role `user` recebe `PROFILE_READ` e é atribuída automaticamente no cadastro público. A seed do administrador preserva contas existentes e falha se o email ou username já estiver em uso por outro cadastro.

O cadastro e o login retornam `data.token`. Envie esse JWT no cabeçalho `Authorization: Bearer <token>` nas rotas protegidas. As senhas novas devem ter pelo menos 8 caracteres e até 72 bytes em UTF-8, o limite do BCrypt.

| Método e rota | Permissão |
| --- | --- |
| `POST /api/v1/auth/sign-up` | Público. Recebe `name`, `email`, `username` e `password` |
| `POST /api/v1/auth/sign-in` | Público. Recebe `email` e `password` |
| `GET /api/v1/profile` | `PROFILE_READ` |
| `GET /api/v1/users` | `USER_READ` |
| `PUT /api/v1/users/{id}/role` | `USER_UPDATE` e role `admin`. Recebe `roleId` |
| `GET /api/v1/roles` | `ROLE_READ` |
| `POST /api/v1/roles` | `ROLE_CREATE`. Recebe `name`, `code` e `level` |
| `PUT /api/v1/roles/{id}` | `ROLE_UPDATE`. Recebe `name`, `code` e `level` |
| `GET /api/v1/permissions` | `PERMISSION_READ` |
| `POST /api/v1/permissions` | `PERMISSION_CREATE`. Recebe `name`, `code` e `description` |
| `GET /api/v1/role-permissions` | `ROLEPERMISSION_READ` |
| `POST /api/v1/role-permissions` | `ROLEPERMISSION_CREATE`. Recebe `roleId` e `permissionId` |
| `DELETE /api/v1/role-permissions/{id}` | `ROLEPERMISSION_DELETE`. Usa o ID do vínculo |

Use `@RequiresPermission(Permission.ROLE_READ)` nos métodos dos controllers. A API consulta a role atual do usuário e seus vínculos no banco a cada requisição protegida. Concessões, revogações e mudanças de role passam a valer nas próximas requisições, inclusive com tokens já emitidos. Uma role nova começa sem permissões. Para proteger uma rota com uma nova permissão, inclua o código no enum `Permission`, adicione uma migration e anote o método.

A role `admin` é atribuída pela seed e seus vínculos são definidos pelas migrations. Quem recebe permissão para gerenciar vínculos em outras roles só pode conceder ou revogar permissões que também possui. O administrador pode gerenciar qualquer permissão nessas roles.

## Testes e CI

Execute `.\mvnw.cmd verify` no Windows ou `./mvnw verify` no Linux/macOS. Por padrão, os testes de integração usam H2 em memória. A CI do GitHub Actions executa a mesma suíte com Java 17 e PostgreSQL 17 em pushes e pull requests para `main`.

Para executar a suíte com PostgreSQL local, aponte `NEXTRADE_TEST_DB_URL`, `NEXTRADE_TEST_DB_USERNAME` e `NEXTRADE_TEST_DB_PASSWORD` para um banco de testes vazio.
