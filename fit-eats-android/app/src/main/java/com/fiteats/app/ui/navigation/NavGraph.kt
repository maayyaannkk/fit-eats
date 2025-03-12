package com.fiteats.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fiteats.app.ui.screens.LoginScreen
import com.fiteats.app.ui.screens.MainScreen
import com.fiteats.app.ui.screens.RegisterScreen
import com.fiteats.app.ui.screens.SplashScreen
import com.fiteats.app.ui.screens.goals.AddMainGoalScreen
import com.fiteats.app.ui.screens.goals.AddWeeklyGoalScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.SplashScreen.route,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(250)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                tween(250)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(250)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                tween(250)
            )
        }) {
        composable(Screens.SplashScreen.route) { SplashScreen(navController) }
        composable(Screens.LoginScreen.route) { LoginScreen(navController) }
        composable(Screens.RegisterScreen.route) { RegisterScreen(navController) }
        composable(Screens.MainScreen.route) { MainScreen(navController) }
        composable(Screens.AddMainGoalScreen.route) { AddMainGoalScreen(navController) }
        composable(Screens.AddWeeklyGoalScreen.route) { AddWeeklyGoalScreen(navController) }
    }
}