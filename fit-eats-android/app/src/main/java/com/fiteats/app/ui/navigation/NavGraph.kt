package com.fiteats.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fiteats.app.models.MainGoalModel
import com.fiteats.app.models.Meal
import com.fiteats.app.ui.screens.LoginScreen
import com.fiteats.app.ui.screens.MainScreen
import com.fiteats.app.ui.screens.RegisterScreen
import com.fiteats.app.ui.screens.SplashScreen
import com.fiteats.app.ui.screens.goals.AddMainGoalScreen
import com.fiteats.app.ui.screens.goals.AddWeeklyGoalScreen
import com.fiteats.app.ui.screens.meal.MealDetailsScreen
import com.fiteats.app.utils.GsonUtil

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
        composable(
            route = Screens.AddWeeklyGoalScreen.route + "?goal={mainGoal}",
            arguments = listOf(navArgument("mainGoal") { type = NavType.StringType })
        ) { backStackEntry ->
            val mainGoalJson = backStackEntry.arguments?.getString("mainGoal")
            val user = mainGoalJson?.let {
                GsonUtil.gson.fromJson<MainGoalModel>(
                    mainGoalJson,
                    MainGoalModel::class.java
                )
            }
            user?.let { AddWeeklyGoalScreen(navController, it) }
        }
        composable(
            route = Screens.MealDetailScreen.route + "?meal={mealDetail}",
            arguments = listOf(navArgument("mealDetail") { type = NavType.StringType })
        ) { backStackEntry ->
            val mealDetailJson = backStackEntry.arguments?.getString("mealDetail")
            val meal = mealDetailJson?.let {
                GsonUtil.gson.fromJson<Meal>(
                    mealDetailJson,
                    Meal::class.java
                )
            }
            meal?.let { MealDetailsScreen(navController, it) }
        }
    }
}