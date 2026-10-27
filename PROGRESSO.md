# PROGRESSO — StudyTasks (backend + offline + quiz)

## Estado atual
- Fase 1 concluída e validada: `./gradlew :backend:run` responde `pong` em `GET /ping`.
- Progresso estimado da Fase 1: 100%.
- Próxima sessão começa na Fase 2 (modelo `Task` compartilhado + CRUD no backend ligado ao MongoDB).

## Tarefas concluídas
**Fase 1**
- [x] Módulo `shared` criado (Kotlin puro, plugin `kotlin.jvm` + `kotlin.serialization` via version catalog, dependência `kotlinx-serialization-json`)
- [x] `Task` (data class, `@Serializable`, campos `id/title/category/isDone`) em `shared/src/main/kotlin/com/game/studytasks/shared/Task.kt`
- [x] `settings.gradle.kts` corrigido: `include(":shared")` e `include(":app")` como linhas próprias (não dentro do `content {}` do repositório `google`)
- [x] Módulo `backend` criado: `build.gradle.kts` com `kotlin.jvm` + `application`, dependendo de `:shared`, `ktor-server-core` e `ktor-server-netty` (via version catalog, versão do Ktor `3.0.3`)
- [x] `build.gradle.kts` da raiz limpo (removida duplicata de `kotlin("jvm") version "2.3.20"`, bloco `repositories{}` indevido — proibido pelo `FAIL_ON_PROJECT_REPOS` — e `dependencies`/`kotlin{}` soltos que não pertenciam à raiz)
- [x] `./gradlew :shared:build` e `./gradlew :backend:build` passando
- [x] `Main.kt` escrito em `backend/src/main/kotlin/com/game/studytasks/backend/Main.kt` com `embeddedServer(Netty) { routing { get("/ping") { call.respondText("pong") } } }`
- [x] `./gradlew :backend:run` respondendo `pong` em `GET /ping` (confirmado via `curl`)

## Próxima tarefa
Iniciar Fase 2: usar o `Task` do módulo `shared` para montar as rotas CRUD (`GET/POST/PUT/DELETE /tasks`) no `backend`, ligadas ao MongoDB Atlas (driver oficial Kotlin). Critério de pronto: CRUD completo testável via curl/Postman.

## Conceitos
**Já domina (do App_Pokemon):** Compose, ViewModel+StateFlow, Retrofit, Room, Repository offline-first, coroutines, testes com fakes.
**Novos neste projeto — em progresso:** módulos Gradle multi-módulo (domina: por que `shared` existe, por que `app`/`backend` não podem depender um do outro), Ktor (agora rodando e respondendo de verdade), driver Kotlin do MongoDB (ainda não começou), deploy de backend (ainda não começou).
**Confundiu uma vez, corrigido:** o que o plugin de compilador (`kotlin.plugin.serialization`) faz vs. o que a biblioteca (`kotlinx-serialization-json`) faz — respondeu invertido na primeira tentativa, reforçar se aparecer de novo.
**Perguntas de fim de fase (Fase 1) ainda pendentes de resposta:** por que `shared` existe separado; `kotlin.plugin.serialization` vs `kotlinx-serialization-json`; por que `ktor-server-core` + `ktor-server-netty` juntos. Retomar essas 3 antes de aprofundar na Fase 2.

## Decisões tomadas
- Sem fila de sincronização: escrita exige internet, leitura funciona offline (decisão de escopo, documentar no README quando chegar lá).
- `shared`, `app` e `backend` são módulos irmãos (nenhum dentro do outro) — `shared` é Kotlin puro pra ser usado pelos dois sem conflito de plataforma.
- Removido o plugin oficial do Ktor (`io.ktor.plugin`) do `backend` por enquanto: ele traz o Shadow (empacotamento de fat jar) embutido, e essa versão do Shadow é incompatível com o Gradle 9.8 (erro em `mainClassName`). Backend roda hoje só com o plugin `application` padrão do Gradle + as dependências do Ktor direto. Revisitar na Fase 3 (deploy) se precisar de fat jar — nessa hora, ver se uma versão mais nova do Ktor/Shadow já corrigiu isso, ou usar `installDist`/`distZip` do próprio `application` em vez do Shadow.

## Dificuldades e aprendizados
- `libs.versions.toml` e `build.gradle.kts` precisam ser commitados juntos.
- Versão do Android Studio precisa ser compatível com a do AGP do projeto.
- Alterações em duas máquinas: sempre `git pull` antes de começar e `git push` ao terminar.
- A pasta `.idea/` foi commitada sem querer; deveria estar no `.gitignore`.
- Antes de commitar: rodar o teste, ver `git status` e usar `git add` com caminhos específicos (nunca `-A` na raiz).
- Terminal: `git --no-pager` evita o paginador (`:`); sair dele com `q`.
- No notebook (Linux), o terminal não herda o JDK do Android Studio automaticamente — precisa exportar `JAVA_HOME` apontando pro `jbr` embutido do Android Studio (`find / -iname jbr`), ou instalar um JDK via `apt`.
- `include(":modulo")` tem que ser uma linha independente no `settings.gradle.kts` — nunca dentro de um bloco `content {}` de repositório.
- Plugins registrados com `apply false` na raiz não podem ser duplicados nem ter versões divergentes em outro lugar do mesmo arquivo.
- Com `repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)` no `settings.gradle.kts`, nenhum módulo (nem a raiz) pode declarar `repositories {}` no próprio `build.gradle.kts`.
- Criar manualmente as pastas de um módulo Kotlin de fora pra dentro é fácil de errar: ao já estar dentro de `backend/`, rodar `mkdir -p backend/src/...` de novo cria um `backend/backend/` duplicado — o Gradle não encontra os fontes porque o source set esperado é `backend/src/main/kotlin`, relativo à raiz do próprio módulo.
- Convenção do Kotlin: a pasta espelha o pacote (`com/game/studytasks/backend/Main.kt` para `package com.game.studytasks.backend`). O compilador não exige isso (usa o `package` declarado no arquivo), mas o Android Studio avisa/sugere mover, então vale manter certo desde o início.
- `embeddedServer`, `Netty`, `routing`, `get` e `call` todos "Unresolved reference" ao mesmo tempo quase sempre é import faltando, não dependência quebrada no `build.gradle.kts` — `call` em particular não precisa de import (é o receiver implícito do lambda de rota), mas só resolve se `routing`/`get` já estiverem resolvidos.
