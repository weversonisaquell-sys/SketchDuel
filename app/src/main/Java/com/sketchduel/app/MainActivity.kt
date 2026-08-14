package com.sketchduel.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sketchduel.app.ui.DrawingScreen
import com.sketchduel.app.ui.HomeScreen
import com.sketchduel.app.ui.MultiplayerScreen
import com.sketchduel.app.ui.theme.SketchDuelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SketchDuelTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                onSolo = { id -> navController.navigate("draw/$id") },
                                onMultiplayer = { id -> navController.navigate("versus/$id") }
                            )
                        }
                        composable(
                            "draw/{lessonId}",
                            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                            DrawingScreen(lessonId = id, onBack = { navController.popBackStack() })
                        }
                        composable(
                            "versus/{lessonId}",
                            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("lessonId") ?: return@composable
                            MultiplayerScreen(lessonId = id, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}

