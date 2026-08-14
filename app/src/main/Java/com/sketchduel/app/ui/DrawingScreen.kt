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
import com.sketchduel.app.logic.ScoreCalculator

@Composable
fun DrawingScreen(lessonId: String, onBack: () -> Unit) {
    val lesson = remember { LessonRepository.byId(lessonId) } ?: return

    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf(listOf<Offset>()) }
    var referenceVisible by remember { mutableStateOf(true) }
    var score by remember { mutableStateOf<Int?>(null) }
    var canvasSize by remember { mutableStateOf(Offset(1f, 1f)) }

    fun normalized(points: List<Offset>) = points.map {
        Offset(it.x / canvasSize.x, it.y / canvasSize.y)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Voltar") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset -> currentStroke = listOf(offset) },
                                onDrag = { change, _ ->
                                    currentStroke = currentStroke + change.position
                                },
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
                            drawPolyline(path, Color.LightGray, 6f)
                        }
                    }

                    (strokes + listOf(currentStroke)).forEach { stroke ->
                        drawPolyline(stroke, Color(0xFF6750A4), 8f)
                    }
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = { referenceVisible = !referenceVisible }) {
                    Text(if (referenceVisible) "Baixar referência" else "Mostrar referência")
                }
                OutlinedButton(onClick = {
                    strokes = if (strokes.isNotEmpty()) strokes.dropLast(1) else strokes
                }) { Text("Desfazer") }
                OutlinedButton(onClick = { strokes = listOf() }) { Text("Limpar") }
            }

            Button(
                onClick = {
                    val normalizedUser = strokes.map { normalized(it) }
                    score = ScoreCalculator.calculate(lesson.strokes, normalizedUser)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) { Text("Finalizar e pontuar") }

            score?.let {
                Text(
                    "Sua pontuação: $it/100",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

private fun DrawScope.drawPolyline(points: List<Offset>, color: Color, width: Float) {
    if (points.size < 2) return
    for (i in 0 until points.size - 1) {
        drawLine(color = color, start = points[i], end = points[i + 1], strokeWidth = width, cap = StrokeCap.Round)
    }
}

