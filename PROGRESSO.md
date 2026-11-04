# PROGRESSO — StudyTasks (backend + offline + quiz)

## Estado atual
- Fases 1–3 concluídas na prática: backend Ktor + MongoDB, Dockerfile, deploy no Render (`https://studytaks.onrender.com/`).
- App Android já consome a API via Retrofit (GET/POST/PUT/DELETE), com ViewModel + Repository.
- Room / offline-first ainda **não** existe — a tela fala direto com a API.
- Progresso estimado do projeto: ~55%.
- Próxima prioridade: Fase 4 (Room como cache de leitura + padrão offline-first do App_Pokemon).

## Tarefas concluídas
**Fase 1**
- [x] Módulos `shared` e `backend`; `GET /ping` → `pong`

**Fase 2**
- [x] `Task` compartilhado (`shared/.../Tasks.kt` — arquivo Tasks.kt, data class `Task`)
- [x] CRUD no backend (`GET/POST/PUT/DELETE /tasks`) com MongoDB Atlas (driver Kotlin coroutine)
- [x] ContentNegotiation + kotlinx.serialization no Ktor

**Fase 3**
- [x] Dockerfile + deploy no Render
- [x] App apontando `baseUrl` para o Render

**App (parcial, entre Fases 3 e 4)**
- [x] `TaskApiService`, `TaskRetrofitClient`, `TaskRepository`, `TaskViewModel`
- [x] UI: listar, toggle `isDone`, criar, deletar (último commit: botão Remover com problema de layout; não testado no dispositivo)
- [ ] Room + leitura offline-first
- [ ] Tratamento de estados loading/erro na UI
- [ ] Remover `StudyTask` local morto em `MainActivity` (modelo antigo, estado local)

## Próxima tarefa
Fase 4: introduzir Room como cache. Leitura sempre do Room; escrita chama a API e, se ok, atualiza o Room. Sem fila de sync. Critério: tasks sobrevivem a reiniciar o app e a ficar offline (só leitura).

Antes disso (opcional, se quiser fechar o que ficou pendente): corrigir layout do botão Remover e testar delete no dispositivo.

## Conceitos
**Já domina (do App_Pokemon):** Compose, ViewModel+StateFlow, Retrofit, Room, Repository offline-first, coroutines, testes com fakes.
**Neste projeto — já usou:** multi-módulo Gradle, Ktor + rotas CRUD, MongoDB driver Kotlin, ContentNegotiation, Retrofit + kotlinx.serialization no app, deploy Render/Docker.
**Ainda não começou aqui:** Room neste app, aba Quiz (Open Trivia DB), testes Ktor / fakes no Repository, README final.
**Perguntas de fim de Fase 1 (ainda pendentes):** por que `shared` separado; plugin vs lib de serialization; por que `ktor-server-core` + `ktor-server-netty`. Pode retomar no fim da Fase 4 ou quando sobrar tempo.

## Decisões tomadas
- Sem fila de sincronização: escrita exige internet; leitura offline via Room (quando existir).
- `shared` / `app` / `backend` irmãos; `shared` é Kotlin puro.
- Backend sem plugin oficial do Ktor (Shadow incompatível com Gradle 9.8); roda com `application` + dependências Ktor. Dockerfile para o Render.
- URL pública: `https://studytaks.onrender.com/` (typo no nome do serviço — manter consistente até decidir renomear).

## Dificuldades e aprendizados
(ver histórico das sessões anteriores no arquivo; pontos que continuam válidos)
- `libs.versions.toml` e `build.gradle.kts` commitados juntos; `git pull` antes de começar em outra máquina.
- `FAIL_ON_PROJECT_REPOS`: sem `repositories {}` nos módulos.
- Pasta de fontes espelha o pacote; Unresolved reference em massa no Ktor costuma ser import.
- Botão Remover: layout na Row do Card ainda problemático (commit `02f674a`).
