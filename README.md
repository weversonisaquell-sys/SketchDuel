# SketchDuel

App de treino de desenho inspirado no ArtWorkout: você traça um desenho de
referência, o app calcula sua precisão, e dá pra duelar contra outro
"jogador" — se ninguém for encontrado, entra um bot automaticamente.

## O que já funciona
- Traçar por cima de uma referência (círculo, estrela, coração, casa, gato)
- Botão para "baixar" (esconder) a referência e ver seu desenho puro
- Desfazer / limpar traços
- Pontuação de 0 a 100 (precisão + quanto da referência foi coberta)
- Modo duelo: procura oponente por alguns segundos e, se não achar, cria um
  bot com progresso e pontuação simulados

## O que falta pra virar o app completo
- Multiplayer *de verdade* (hoje é só bot) — precisa de um backend, por
  exemplo Firebase Realtime Database ou Firestore, pra fazer o matchmaking
  entre jogadores reais
- Login de usuário e progresso salvo na nuvem
- Mais lições e categorias (hoje são só 5 formas geradas por código)
- Zoom/pincel com pressão, loja de itens, assinatura, etc.

## Como compilar

Este repositório já vem com um workflow do GitHub Actions
(`.github/workflows/android-build.yml`) que compila o APK sozinho a cada
push. Você não precisa instalar Android Studio:

1. Crie um repositório novo no GitHub e suba esta pasta inteira nele.
2. Vá na aba **Actions** do repositório — o build começa automaticamente.
3. Quando terminar (ícone verde), abra o job, desça até **Artifacts** e
   baixe `sketchduel-debug-apk`.
4. Descompacte, transfira o `.apk` pro celular e instale (pode precisar
   permitir "instalar de fontes desconhecidas").

### Compilar localmente (opcional)
Se quiser testar no seu PC com Android Studio instalado, é só abrir a
pasta do projeto — o Android Studio já reconhece o `build.gradle.kts` e
oferece pra rodar/depurar direto.
