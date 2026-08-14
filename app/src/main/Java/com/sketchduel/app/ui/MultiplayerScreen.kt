package com.sketchduel.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sketchduel.app.data.LessonRepository
import com.sketchduel.app.logic.BotSimulator
import com.sketchduel.app.logic.ScoreCalculator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class MatchState { SEARCHING, BOT_FOUND, DRAWING, FINISHED }

@Composable
fun MultiplayerScreen(lessonId: String, onBack: () -> Unit) {
    val lesson = remember { LessonRepository.byId(lessonId) } ?: return
    var state by remember { mutableStateOf(MatchState.SEARCHING) }
    var botProgress by remember { mutableStateOf(0f) }
    var botScore by remember { mutableStateOf<Int?>(null) }
    var userScore by remember { mutableStateOf<Int?>(null) }

    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf(listOf<Offset>()) }
    var referenceVisible by remember { mutableStateOf(true) }
    var canvasSize by remember { mutableStateOf(Offset(1f, 1f)) }
    val scope = rememberCoroutineScope()

    // Tenta achar um oponente real por alguns segundos; como ainda não há
    // um servidor de matchmaking, sempre cai para um bot. Quando um backend
    // (ex.: Firebase) existir, é só trocar essa busca por uma real — o resto
    // da tela de duelo não muda.
    LaunchedEffect(Unit) {
        delay(2500)
        state = MatchState.BOT_FOUND
        delay(1200)
        state = MatchState.DRAWING
        scope.launch {
            BotSimulator.race(lesson.difficulty).collect { result ->
                botProgress = result.progress
                if (result.finished) botScore = result.score
            }
        }
    }

    fun normalized(points: List<Offset>) = points.map { Offset(it.x / canvasSize.x, it.y / canvasSize.y) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duelo: ${lesson.title}") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") } }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            when (state) {
                MatchState.SEARCHING -> Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Procurando outro jogador...")
                    }
                }

                MatchState.BOT_FOUND -> Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ninguém disponível agora. Você vai duelar contra um bot!", style = MaterialTheme.typography.bodyLarge)
                }

                else -> {
                    Text("Oponente (bot): ${(botProgress * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
                    LinearProgressIndicator(
                        progress = { botProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { offset -> currentStroke = listOf(offset) },
                                        onDrag = { change, _ -> currentStroke = currentStroke + change.position },
                                        onDragEnd = {
                                            strokes = strokes + listOf(currentStroke)
                                            currentStroke = listOf()
                                        }
                                    )
                                }
                        ) {
                            canvasSize = Offset(size.width, size.height)
                            if (referenceVisible) {
                                lesson.strokes.forEach { stroke ->
                                    val path = stroke.map { Offset(it.x * size.width, it.y * size.height) }
                                    drawPolylineMp(path, Color.LightGray, 6f)
                                }
                            }
                            (strokes + listOf(currentStroke)).forEach { stroke ->
                                drawPolylineMp(stroke, Color(0xFF6750A4), 8f)
                            }
                        }
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        OutlinedButton(onClick = { referenceVisible = !referenceVisible }) {
                            Text(if (referenceVisible) "Baixar referência" else "Mostrar referência")
                        }
                        OutlinedButton(onClick = { strokes = if (strokes.isNotEmpty()) strokes.dropLast(1) else strokes }) { Text("Desfazer") }
                    }

                    Button(
                        onClick = {
                            val normalizedUser = strokes.map { normalized(it) }
                            userScore = ScoreCalculator.calculate(lesson.strokes, normalizedUser)
                            state = MatchState.FINISHED
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) { Text("Finalizar duelo") }
                }
            }

            if (state == MatchState.FINISHED && userScore != null) {
                val bot = botScore ?: 0
                val you = userScore ?: 0
                val resultText = when {
                    you > bot -> "Você venceu! 🏆"
                    you < bot -> "O bot venceu desta vez."
                    else -> "Empate!"
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(resultText, style = MaterialTheme.typography.titleLarge)
                    Text("Você: $you  •  Bot: $bot", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

private fun DrawScope.drawPolylineMp(points: List<Offset>, color: Color, width: Float) {
    if (points.size < 2) return
    for (i in 0 until points.size - 1) {
        drawLine(color = color, start = points[i], end = points[i + 1], strokeWidth = width, cap = StrokeCap.Round)
    }
}
