package com.ruzibekov.quran.transcription

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.ruzibekov.quran.transcription.ui.screens.HomeScreen
import com.ruzibekov.quran.transcription.ui.screens.SurahDetailScreen
import com.ruzibekov.quran.transcription.ui.screens.SurahDetailViewModel
import com.ruzibekov.quran.transcription.ui.theme.QuranTranscriptionTheme
import androidx.navigation.NavType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuranTranscriptionTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = AppRoute.Home.route,
                ) {
                    composable(AppRoute.Home.route) {
                        HomeScreen(
                            onSurahSelected = { surahId ->
                                navController.navigate(AppRoute.SurahDetail.createRoute(surahId))
                            },
                        )
                    }
                    composable(
                        route = AppRoute.SurahDetail.route,
                        arguments = listOf(navArgument(SurahDetailViewModel.ARG_SURAH_ID) { type = NavType.IntType }),
                    ) { backStackEntry ->
                        val surahId = backStackEntry.arguments?.getInt(SurahDetailViewModel.ARG_SURAH_ID)
                            ?: return@composable
                        SurahDetailScreen(
                            onNavigateBack = { navController.popBackStack() },
                            surahId = surahId,
                        )
                    }
                }
            }
        }
    }
}


sealed class AppRoute(val route: String) {
    object Home : AppRoute("home-route")
    object SurahDetail : AppRoute("surah-detail-route/{surahId}") {
        fun createRoute(surahId: Int) = "surah-detail-route/$surahId"
    }
}
