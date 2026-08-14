package com.sketchduel.app.data

import androidx.compose.ui.geometry.Offset
import com.sketchduel.app.model.Lesson
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/**
 * Todas as lições aqui são geradas por fórmula (círculo, estrela, coração,
 * etc.), então não usamos nenhuma imagem ou traçado de terceiros — é fácil
 * adicionar novas lições sem depender de arte externa.
 */
private fun circle(cx: Float, cy: Float, r: Float, points: Int = 60): List<Offset> =
    (0..points).map {
        val a = 2 * PI * it / points
        Offset((cx + r * cos(a)).toFloat(), (cy + r * sin(a)).toFloat())
    }

private fun star(cx: Float, cy: Float, rOuter: Float, rInner: Float, spikes: Int = 5): List<Offset> {
    val pts = mutableListOf<Offset>()
    val step = PI / spikes
    var rot = -PI / 2
    repeat(spikes) {
        pts.add(Offset((cx + cos(rot) * rOuter).toFloat(), (cy + sin(rot) * rOuter).toFloat()))
        rot += step
        pts.add(Offset((cx + cos(rot) * rInner).toFloat(), (cy + sin(rot) * rInner).toFloat()))
        rot += step
    }
    pts.add(pts.first())
    return pts
}

private fun heart(cx: Float, cy: Float, scale: Float, points: Int = 60): List<Offset> =
    (0..points).map {
        val t = 2 * PI * it / points
        val x = 16 * sin(t).pow(3)
        val y = -(13 * cos(t) - 5 * cos(2 * t) - 2 * cos(3 * t) - cos(4 * t))
        Offset((cx + x * scale / 16).toFloat(), (cy + y * scale / 16).toFloat())
    }

private fun house(cx: Float, cy: Float, w: Float, h: Float): List<List<Offset>> {
    val base = listOf(
        Offset(cx - w / 2, cy + h / 2),
        Offset(cx - w / 2, cy - h / 4),
        Offset(cx, cy - h / 2 - h / 4),
        Offset(cx + w / 2, cy - h / 4),
        Offset(cx + w / 2, cy + h / 2),
        Offset(cx - w / 2, cy + h / 2)
    )
    val door = listOf(
        Offset(cx - w / 8, cy + h / 2),
        Offset(cx - w / 8, cy + h / 8),
        Offset(cx + w / 8, cy + h / 8),
        Offset(cx + w / 8, cy + h / 2)
    )
    return listOf(base, door)
}

object LessonRepository {
    val lessons: List<Lesson> = listOf(
        Lesson("star", "Estrela", "Formas", 1, listOf(star(0.5f, 0.45f, 0.3f, 0.13f))),
        Lesson("circle", "Círculo Perfeito", "Formas", 1, listOf(circle(0.5f, 0.45f, 0.28f))),
        Lesson("heart", "Coração", "Formas", 1, listOf(heart(0.5f, 0.42f, 0.9f))),
        Lesson("house", "Casinha", "Objetos", 2, house(0.5f, 0.5f, 0.5f, 0.4f)),
        Lesson(
            "cat", "Rosto de Gato", "Animais", 2,
            listOf(
                circle(0.5f, 0.5f, 0.25f),
                listOf(Offset(0.32f, 0.35f), Offset(0.28f, 0.18f), Offset(0.4f, 0.3f)),
                listOf(Offset(0.68f, 0.35f), Offset(0.72f, 0.18f), Offset(0.6f, 0.3f))
            )
        )
    )

    fun byId(id: String) = lessons.firstOrNull { it.id == id }
}
