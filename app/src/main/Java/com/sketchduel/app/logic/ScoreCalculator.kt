package com.sketchduel.app.logic

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

object ScoreCalculator {

    /**
     * Compara os traços do usuário com o traçado de referência da lição.
     * Ambos devem estar no mesmo espaço de coordenadas normalizado (0f..1f).
     * Retorna uma pontuação de 0 a 100, combinando precisão (quão perto o
     * traço do usuário fica da referência) e cobertura (quanto da
     * referência foi de fato percorrida).
     */
    fun calculate(reference: List<List<Offset>>, userStrokes: List<List<Offset>>): Int {
        val refPoints = reference.flatten()
        val userPoints = userStrokes.flatten()
        if (refPoints.isEmpty() || userPoints.isEmpty()) return 0

        val avgDistance = userPoints.map { u ->
            refPoints.minOf { r -> distance(u, r) }
        }.average()
        val precisionScore = (1.0 - (avgDistance / 0.12).coerceIn(0.0, 1.0)).coerceIn(0.0, 1.0)

        val threshold = 0.05f
        val covered = refPoints.count { r ->
            userPoints.any { u -> distance(u, r) < threshold }
        }
        val coverageScore = covered.toDouble() / refPoints.size

        val finalScore = (precisionScore * 0.55 + coverageScore * 0.45) * 100
        return finalScore.toInt().coerceIn(0, 100)
    }

    private fun distance(a: Offset, b: Offset): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }
}

