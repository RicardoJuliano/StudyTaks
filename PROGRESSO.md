# PROGRESSO — StudyTasks (backend + offline + quiz)

## Estado atual
- Fases 1–4 concluídas: backend Ktor + MongoDB (deploy no Render, `https://studytaks.onrender.com/`), app Android com Retrofit + Room offline-first ligado via ViewModel/Repository.
- Progresso estimado do projeto: ~67% (4 de 6 fases).
- Próxima prioridade: Fase 5 (aba de Quiz via Open Trivia DB).

## Tarefas concluídas
**Fase 1**
- [x] Módulos `shared` e `backend`; `GET /ping` → `pong`

**Fase 2**
- [x] `Task` compartilhado (`shared/.../Tasks.kt`)
- [x] CRUD no backend (`GET/POST/PUT/DELETE /tasks`) com MongoDB Atlas (driver Kotlin coroutine)
- [x] ContentNegotiation + kotlinx.serialization no Ktor

**Fase 3**
- [x] Dockerfile + deploy no Render
- [x] App apontando `baseUrl` para o Render

**Fase 4**
- [x] `TaskApiService`, `TaskRetrofitClient`, `TaskRepository`, `TaskViewModel` (Retrofit completo: GET/POST/PUT/DELETE)
- [x] UI: listar, toggle `isDone`, criar, deletar — layout do botão Remover corrigido (`weight()` no lugar certo)
- [x] Room: `TaskEntity`, `TaskDao` (incluindo delete por `id` via `@Query`), `AppDatabase`, `StudyTasksApplication` (singleton via `by lazy`)
- [x] `TaskMapper` (`toTask()` / `toEntity()`) entre `Task` (API) e `TaskEntity` (Room)
- [x] `TaskRepository`: leitura via `observeTask()` (Flow do Room), sincronização via `refreshTask()`, escrita (`create`/`update`/`delete`) grava no Room após sucesso da API
- [x] `TaskViewModel`: `AndroidViewModel` + `getApplication<StudyTasksApplication>()`; `init` com duas corrotinas separadas (collect do Flow + refresh único)
- [x] Testado no dispositivo: tasks sobrevivem a reiniciar o app e a ficar offline (modo avião)
- [x] `.gitignore` corrigido (`.idea/`, `.maestri/` fora do controle de versão)
- [ ] Tratamento de estados loading/erro na UI (`.onFailure` não tratado em nenhuma função do ViewModel — gap conhecido)
- [ ] Validação de formulário (título/categoria vazios ainda são aceitos)
- [ ] Remover `StudyTask` local morto em `MainActivity` (modelo antigo, estado local)
- [ ] `exportSchema` do Room (warning do KSP; setar `false` ou configurar schema location)

## Próxima tarefa
Fase 5: aba de Quiz consumindo a Open Trivia DB (categoria Computers), sem necessidade de offline. Critério: perguntas aparecem e respondem corretamente.

Pendências menores que podem ser resolvidas antes ou durante a Fase 5 (não bloqueiam): `.onFailure` no ViewModel, validação de formulário, limpeza do `StudyTask` morto.

## Conceitos
**Já domina (do App_Pokemon, confirmado neste projeto):** Compose, ViewModel+StateFlow, Retrofit, Room (Entity/Dao/Database), Repository offline-first, coroutines, extension functions como mapper, `Modifier.weight()` em Row/Column.
**Neste projeto — já usou:** multi-módulo Gradle, Ktor + rotas CRUD, MongoDB driver Kotlin, ContentNegotiation, Retrofit + kotlinx.serialization, deploy Render/Docker, Room + AndroidViewModel, KSP (incluindo troca de versão por incompatibilidade com built-in Kotlin do AGP 9).
**Ainda não começou aqui:** aba Quiz (Open Trivia DB), testes Ktor / fakes no Repository, README final.
**Perguntas de fim de Fase 1 (ainda pendentes):** por que `shared` separado; plugin vs lib de serialization; por que `ktor-server-core` + `ktor-server-netty`. Pode retomar no fim do projeto ou quando sobrar tempo.

## Decisões tomadas
- Sem fila de sincronização: escrita exige internet (erro só avisa, não aplica a mudança); leitura sempre offline via Room.
- `insertAll` usa `OnConflictStrategy.REPLACE` — necessário porque `refreshTask()` reinsere tasks já existentes a cada sincronização.
- Delete no `TaskDao` é por `id` via `@Query("DELETE FROM tasks WHERE id = :id")`, não por `@Delete(TaskEntity)` — evita ter que reconstruir a entidade inteira só pra apagar.
- `shared` / `app` / `backend` irmãos; `shared` é Kotlin puro.
- Backend sem plugin oficial do Ktor (Shadow incompatível com Gradle 9.8); roda com `application` + dependências Ktor. Dockerfile para o Render.
- URL pública: `https://studytaks.onrender.com/` (typo no nome do serviço — manter consistente até decidir renomear).
- Render free tier hiberna após inatividade; cold start pode causar timeout silencioso na primeira sincronização (motivo real do `.onFailure` virar prioridade antes da Fase 6).

## Dificuldades e aprendizados
- KSP precisou ser atualizado de `2.2.10-2.0.2` para `2.3.6` — versão antiga não suportava o "built-in Kotlin" do AGP 9 (erro: "Using kotlin.sourceSets DSL... not allowed with built-in Kotlin"). A partir do KSP 2.3.0 a versão não é mais amarrada à versão do Kotlin.
- `Modifier.weight()` só funciona em filho direto de `RowScope`/`ColumnScope` — aplicado na `Row` em vez da `Column` interna, quebrou o layout do botão Remover (e nem compilava, por falta de import).
- `.idea/` e pastas de outras ferramentas de IA (`.maestri/`) foram commitadas por engano; resolvido com `git rm -r --cached` + `.gitignore` genérico (`.idea/`, `.maestri/`).
- `libs.versions.toml` e `build.gradle.kts` commitados juntos; `git pull` antes de começar em outra máquina.
- `FAIL_ON_PROJECT_REPOS`: sem `repositories {}` nos módulos.
- Pasta de fontes espelha o pacote; Unresolved reference em massa no Ktor costuma ser import.
