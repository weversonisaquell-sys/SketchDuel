package com.sketchduel.app.model

import androidx.compose.ui.geometry.Offset

/**
 * Uma lição de desenho. `strokes` guarda o traçado de referência em
 * coordenadas normalizadas (0f..1f), para funcionar em qualquer tamanho
 * de tela.
 */
data class Lesson(
    val id: String,
    val title: String,
    val category: String,
    val difficulty: Int, // 1 a 3
    val strokes: List<List<Offset>>
)
