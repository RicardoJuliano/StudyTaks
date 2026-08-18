# StudyTaks

Aplicativo Android de estudos feito com Kotlin e Jetpack Compose.

A tela principal funciona como uma lista de tarefas de aprendizado: cada item representa um assunto para estudar ou implementar, e o usuario pode marcar o que ja concluiu. O projeto tambem tem navegacao inferior com espacos para tarefas, progresso e perfil.

## O que existe no app

- Lista de tarefas usando `LazyColumn`.
- Cards com checkbox para marcar tarefa como concluida.
- Atualizacao de estado com `remember` e lista mutavel.
- Bottom navigation com abas Tasks, Progress e Profile.
- Tela de progresso simples.
- Tela de perfil simples.
- Tema Android com Material 3.

## Tarefas que aparecem no proprio app

O app lista topicos reais do caminho de estudo:

- Data classes em Kotlin.
- Listas com `LazyColumn`.
- `remember` e `mutableStateOf`.
- ViewModel.
- Room para persistencia local.

## Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android Gradle Plugin

## Rodando

Abra o projeto no Android Studio e execute em um emulador ou aparelho Android.

Pelo terminal:

```bash
./gradlew assembleDebug
```

No Windows:

```bash
gradlew.bat assembleDebug
```

## Observacoes

O estado das tarefas ainda fica em memoria. ViewModel e Room aparecem como proximos passos de estudo, mas ainda nao fazem parte da implementacao atual.
