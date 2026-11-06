# StudyTasks

App Android (Kotlin + Jetpack Compose) para controlar tarefas de estudo, com backend próprio (Ktor + MongoDB) e cache local offline-first (Room). Projeto de prática pessoal, construído em modo tutor com o Claude, para consolidar conceitos de desenvolvimento Android e backend Kotlin.

## Stack

| Camada | Tecnologia |
|---|---|
| App Android | Kotlin, Jetpack Compose, Material3 |
| Estado / UI | ViewModel + StateFlow |
| Rede (app) | Retrofit + kotlinx.serialization |
| Cache local | Room (offline-first, somente leitura) |
| Backend | Ktor (Netty) |
| Banco de dados | MongoDB Atlas (driver oficial Kotlin coroutine) |
| Deploy do backend | Docker + Render |
| Build | Gradle multi-módulo (`app`, `shared`, `backend`), Kotlin version catalog |

## Backend publicado

`https://studytaks.onrender.com/`

> O serviço roda no free tier do Render, que hiberna após um período de inatividade. A primeira requisição depois de um tempo parado pode levar de 30 a 60 segundos para responder (o app trata esse caso mostrando os dados salvos localmente enquanto tenta sincronizar).

Rotas disponíveis:

| Método | Rota | Descrição |
|---|---|---|
| GET | `/ping` | Healthcheck (`pong`) |
| GET | `/tasks` | Lista todas as tasks |
| POST | `/tasks` | Cria uma task |
| PUT | `/tasks/{id}` | Atualiza uma task existente |
| DELETE | `/tasks/{id}` | Remove uma task |

## Arquitetura

```
shared/   → modelo Task (data class, compartilhado entre app e backend)
backend/  → rotas Ktor (GET/POST/PUT/DELETE /tasks), conexão com MongoDB
app/      → Compose UI → ViewModel → Repository → { Room (leitura) + Retrofit (escrita) }
```

O app segue o padrão **offline-first**: a tela nunca fala diretamente com a API. Toda leitura vem do Room; toda escrita (criar, concluir, excluir) chama a API primeiro e só atualiza o Room se a chamada tiver sucesso. Não existe fila de sincronização — se uma escrita falhar (sem internet, backend indisponível), a mudança simplesmente não é aplicada, e o usuário vê uma mensagem de erro.

### Fluxo de leitura (sempre do Room)

```mermaid
flowchart LR
    UI[Compose UI] -->|observa| VM[TaskViewModel]
    VM -->|collect Flow| Repo[TaskRepository.observeTask]
    Repo -->|Flow List TaskEntity| Dao[TaskDao.getAll]
    Dao -->|SELECT * FROM tasks| Room[(Room / SQLite)]
    VM -.->|dispara 1x no init| Refresh[TaskRepository.refreshTask]
    Refresh -->|GET /tasks| API[Backend Ktor]
    API --> Mongo[(MongoDB Atlas)]
    Refresh -->|insertAll REPLACE| Room
```

### Fluxo de escrita (API primeiro, Room depois)

```mermaid
sequenceDiagram
    participant UI as Compose UI
    participant VM as TaskViewModel
    participant Repo as TaskRepository
    participant API as Backend Ktor
    participant Room as Room (SQLite)

    UI->>VM: createTask / toggleTask / deleteTask
    VM->>Repo: chama a função correspondente
    Repo->>API: POST/PUT/DELETE /tasks
    alt sucesso
        API-->>Repo: 200/201/204
        Repo->>Room: insertAll / delete
        Room-->>VM: Flow emite lista atualizada
        VM-->>UI: _tasks.value atualizado
    else falha (sem internet, backend fora)
        API-->>Repo: exceção
        Repo-->>VM: Result.failure
        VM-->>UI: errorMessage exibido, Room não é alterado
    end
```

## Como rodar

### Backend

Requisitos: um cluster MongoDB Atlas (free tier serve) e a variável de ambiente `MONGODB_URI` com a connection string.

```bash
export MONGODB_URI="mongodb+srv://usuario:senha@cluster.mongodb.net/"
./gradlew :backend:run
```

Ou via Docker (mesmo processo usado no deploy):

```bash
docker build -t studytasks-backend .
docker run -e MONGODB_URI="..." -p 8080:8080 studytasks-backend
```

### App Android

1. Abre o projeto no Android Studio.
2. O app já aponta para o backend publicado (`https://studytaks.onrender.com/`) — não precisa de configuração extra pra testar.
3. Pra apontar pro backend local, troca a `baseUrl` em `TaskRetrofitClient.kt`.
4. Roda num emulador ou dispositivo físico com permissão de internet.

## Regras de negócio

- Uma task tem `id` (UUID gerado no app), `title`, `category` e `isDone`.
- Criar, concluir/reabrir e excluir uma task exigem conexão — não há fila de sincronização offline.
- A listagem de tasks sempre funciona offline, mostrando o último estado sincronizado com sucesso.
- Título e categoria em branco não são aceitos na criação de uma task.
- A aba Progress calcula o percentual de tasks concluídas sobre o total, lendo o mesmo estado da aba Tasks.

## Decisões tomadas

| Dúvida | Decisão | Por quê |
|---|---|---|
| Fila de sincronização offline? | Não implementada | Escopo de prática; erro de rede só avisa, não aplica a mudança — simples e previsível |
| `Task` (API) e `TaskEntity` (Room) na mesma classe? | Classes separadas, com `TaskMapper` convertendo entre elas | Cada camada evolui independente, sem acoplar o contrato da API ao schema do banco local |
| Delete no Room por entidade inteira ou por id? | Por `id`, via `@Query("DELETE FROM tasks WHERE id = :id")` | O Repository só tem o `id` disponível nesse ponto; evita reconstruir uma entidade falsa |
| `OnConflictStrategy` do `insertAll` | `REPLACE` | `refreshTask()` reinsere tasks já existentes a cada sincronização; sem isso, a segunda sincronização quebraria por conflito de chave primária |
| Deploy do backend | Docker + Render (free tier) | Conta não oferece runtime nativo JVM; Dockerfile usa o próprio `./gradlew` do projeto pra garantir a versão certa do Gradle |
| Aba de Quiz (Open Trivia DB) | Removida do escopo | Não agregava ao produto (perguntas de cultura geral desconectadas das tasks); tempo melhor investido nos estados de erro e no README |

## Melhorias futuras

- Fila de sincronização para escrita offline (hoje a mudança é descartada se não houver conexão no momento).
- Testes automatizados: Ktor test module no backend, Repository com fakes no app.
- Edição de título/categoria de uma task já existente (hoje só dá pra alternar `isDone` e excluir).
- Evitar a hibernação do Render (ping periódico ou plano pago) para reduzir o atraso na primeira sincronização do dia.
- Revisão visual (cores, tipografia, espaçamento) — adiada de propósito para depois do fluxo funcional estar completo.
