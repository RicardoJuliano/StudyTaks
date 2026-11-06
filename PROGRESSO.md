# PROGRESSO — StudyTasks (backend + offline)

## Estado atual
- Projeto considerado **finalizado** no escopo revisado: backend Ktor + MongoDB (deploy no Render), app Android com Retrofit + Room offline-first, 3 abas (Tasks, Progress, Profile), estados de loading/erro tratados, README publicado.
- **Escopo original mudou em 06/11**: a aba de Quiz (Open Trivia DB) foi removida — decisão do Ricardo, por não agregar ao produto (perguntas soltas, sem ligação com as tasks). Em troca, entraram Progress e Profile (sucintos, sem design ainda) e o tratamento formal de loading/erro.
- Progresso estimado: ~90% do escopo revisado. Falta só testes formais (Fase 6, baixa prioridade) e uma revisão visual (cores/tipografia), adiada de propósito.

## Tarefas concluídas
**Fase 1** — Módulos `shared`/`backend`; `GET /ping` → `pong`

**Fase 2** — `Task` compartilhado; CRUD completo no backend com MongoDB Atlas; ContentNegotiation + kotlinx.serialization

**Fase 3** — Dockerfile + deploy no Render; app apontando pra URL pública

**Fase 4** — Retrofit completo (GET/POST/PUT/DELETE) + Room (`TaskEntity`, `TaskDao`, `AppDatabase`, `StudyTasksApplication`) + `TaskMapper` + `TaskRepository` offline-first (`observeTask`/`refreshTask`) + `TaskViewModel` como `AndroidViewModel`. Testado no dispositivo: sobrevive a restart e modo avião.

**Sessão de 06/11 (pós-Fase 4, quiz removido do escopo)**
- [x] Removido `StudyTask` (modelo morto, estado local antigo)
- [x] Validação de formulário (título/categoria em branco não cria task)
- [x] Aba Progress: `concluidas`/`total`/`percentual` a partir do mesmo `TaskViewModel` das Tasks
- [x] Aba Profile: conteúdo estático simples (nome + descrição), design pendente de propósito
- [x] `TaskViewModel`: `isLoading` e `errorMessage` (`StateFlow`) tratados em `createTask`, `updateTask`, `deleteTask` e no `refreshTask()` do `init`
- [x] UI (`TasksContent`): `CircularProgressIndicator` + mensagem de erro condicionais no topo da lista
- [x] Testado no dispositivo: erro aparece corretamente em modo avião (criar task), task antiga permanece intacta
- [x] README.md (stack, arquitetura, como rodar, fluxogramas Mermaid, regras de negócio, decisões, melhorias futuras)
- [x] Quiz removido do `CLAUDE.md` / escopo do projeto (decisão registrada no README)

## Pendências conhecidas (não bloqueiam, registradas no README)
- Fila de sincronização offline para escrita (fora de escopo por decisão)
- Testes automatizados (Ktor test module, fakes no Repository) — Fase 6, baixa prioridade
- Edição de título/categoria de task existente (hoje só toggle + delete)
- `exportSchema` do Room (warning do KSP, não bloqueante)
- Revisão visual (cores, tipografia, espaçamento) — adiada de propósito

## Próxima tarefa
Nenhuma obrigatória. Se for continuar: revisão visual/design (cores, tipografia) é o que o Ricardo sinalizou que quer explorar em seguida. Testes formais (Fase 6) ficam como opção B.

## Conceitos
**Já domina (confirmado neste projeto):** Compose, ViewModel+StateFlow, Retrofit, Room (Entity/Dao/Database), Repository offline-first, coroutines, extension functions como mapper, `Modifier.weight()`, tratamento de estados loading/erro com StateFlow nullable, `?.let`.
**Usado neste projeto:** multi-módulo Gradle, Ktor + rotas CRUD, MongoDB driver Kotlin, ContentNegotiation, Retrofit + kotlinx.serialization, deploy Render/Docker, Room + AndroidViewModel, KSP (resolveu incompatibilidade com built-in Kotlin do AGP 9).
**Perguntas de fim de Fase 1 (ainda pendentes, nunca retomadas):** por que `shared` separado; plugin vs lib de serialization; por que `ktor-server-core` + `ktor-server-netty`.

## Decisões tomadas
- Sem fila de sincronização: escrita exige internet (erro só avisa, não aplica a mudança); leitura sempre offline via Room.
- `insertAll` usa `OnConflictStrategy.REPLACE` — necessário porque `refreshTask()` reinsere tasks já existentes a cada sincronização.
- Delete no `TaskDao` é por `id` via `@Query`, não por `@Delete(TaskEntity)`.
- Quiz (Open Trivia DB) removido do escopo em 06/11 — decisão do Ricardo, registrada no README.
- Mensagens de erro diferenciadas: "verifique sua conexão" pras escritas (ação não aconteceu), "mostrando dados salvos" pro refresh inicial (ainda há algo pra mostrar).
- `shared` / `app` / `backend` irmãos; `shared` é Kotlin puro.
- Backend sem plugin oficial do Ktor (Shadow incompatível com Gradle 9.8); roda com `application` + dependências Ktor. Dockerfile usa `./gradlew` do próprio projeto.
- URL pública: `https://studytaks.onrender.com/` (typo no nome do serviço — mantido por consistência).
- Render free tier hiberna após inatividade; cold start pode causar timeout na primeira sincronização — por isso o `.onFailure` do `refreshTask()` tem mensagem diferenciada.

## Dificuldades e aprendizados
- KSP precisou ir de `2.2.10-2.0.2` pra `2.3.6` — versão antiga não suportava o "built-in Kotlin" do AGP 9. A partir do KSP 2.3.0 a versão não é mais amarrada à do Kotlin.
- `Modifier.weight()` só funciona em filho direto de `RowScope`/`ColumnScope` — aplicado na `Row` em vez da `Column` interna, quebrou o botão Remover (e nem compilava, por falta de import).
- `.idea/` e `.maestri/` (config de outra ferramenta) foram commitados por engano; resolvido com `git rm -r --cached` + `.gitignore` genérico.
- Sem tratar `.onFailure`, uma falha de rede no `refreshTask()` inicial (ex.: Render hibernado) deixava a tela vazia sem nenhum aviso — bug real encontrado e corrigido durante o projeto, não só teórico.
- Pasta de fontes espelha o pacote; Unresolved reference em massa no Ktor costuma ser import.
