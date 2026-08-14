package com.sketchduel.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sketchduel.app.data.LessonRepository
import com.sketchduel.app.model.Lesson

@Composable
fun HomeScreen(onSolo: (String) -> Unit, onMultiplayer: (String) -> Unit) {
    var chosen by remember { mutableStateOf<Lesson?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text("SketchDuel") }) }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(LessonRepository.lessons) { lesson ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { chosen = lesson },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(lesson.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("${lesson.category} • Dificuldade ${lesson.difficulty}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

    chosen?.let { lesson ->
        AlertDialog(
            onDismissRequest = { chosen = null },
            title = { Text(lesson.title) },
            text = { Text("Como você quer praticar?") },
            confirmButton = {
                TextButton(onClick = {
                    val id = lesson.id
                    chosen = null
                    onSolo(id)
                }) { Text("Solo") }
            },
            dismissButton = {
                TextButton(onClick = {
                    val id = lesson.id
                    chosen = null
                    onMultiplayer(id)
                }) { Text("Multiplayer") }
            }
        )
    }
}

