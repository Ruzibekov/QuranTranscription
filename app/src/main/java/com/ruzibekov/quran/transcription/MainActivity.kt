package com.ruzibekov.quran.transcription

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ruzibekov.quran.transcription.ui.screens.detail.SurahDetailScreen
import com.ruzibekov.quran.transcription.ui.screens.detail.SurahDetailViewModel
import com.ruzibekov.quran.transcription.ui.screens.home.HomeScreen
import com.ruzibekov.quran.transcription.ui.theme.QuranTranscriptionTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                    composable(
                        route = AppRoute.Home.route,
                        exitTransition = {
                            fadeOut(animationSpec = tween(200))
                        },
                        popEnterTransition = {
                            fadeIn(animationSpec = tween(250))
                        },
                    ) {
                        HomeScreen(
                            onSurahSelected = { surahId ->
                                navController.navigate(AppRoute.SurahDetail.createRoute(surahId))
                            },
                        )
                    }
                    composable(
                        route = AppRoute.SurahDetail.route,
                        arguments = listOf(navArgument(SurahDetailViewModel.ARG_SURAH_ID) { type = NavType.IntType }),
                        enterTransition = {
                            slideInHorizontally(
                                initialOffsetX = { it / 3 },
                                animationSpec = tween(300),
                            ) + fadeIn(animationSpec = tween(300))
                        },
                        popExitTransition = {
                            slideOutHorizontally(
                                targetOffsetX = { it / 3 },
                                animationSpec = tween(250),
                            ) + fadeOut(animationSpec = tween(250))
                        },
                    ) { backStackEntry ->
                        if (backStackEntry.arguments?.containsKey(SurahDetailViewModel.ARG_SURAH_ID) != true) {
                            return@composable
                        }
                        SurahDetailScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateNext = { nextSurahId ->
                                navController.navigate(AppRoute.SurahDetail.createRoute(nextSurahId)) {
                                    popUpTo(AppRoute.SurahDetail.route) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                            },
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
