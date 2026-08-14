package com.sketchduel.app.logic

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

data class BotResult(val progress: Float, val finished: Boolean, val score: Int = 0)

/**
 * Simula um oponente enquanto não existe um servidor real de matchmaking.
 * Quando o app tiver um backend (ex.: Firebase), esta é a peça a trocar
 * por uma busca de partida de verdade — o resto da tela de duelo não
 * precisa mudar.
 */
object BotSimulator {
    fun race(difficulty: Int) = flow {
        val skill = Random.nextInt(55, 96)
        val durationMs = (2500..5500).random()
        val steps = 20
        val stepDelay = durationMs / steps
        for (i in 1..steps) {
            delay(stepDelay.toLong())
            emit(BotResult(progress = i / steps.toFloat(), finished = i == steps, score = if (i == steps) skill else 0))
        }
    }
}

