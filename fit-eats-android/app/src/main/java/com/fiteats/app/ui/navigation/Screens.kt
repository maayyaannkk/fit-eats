package com.fiteats.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Screens(val route: String) {
    object SplashScreen : Screens("splash")
    object LoginScreen : Screens("login")
    object RegisterScreen : Screens("register")
    object MainScreen : Screens("main")
    object AddMainGoalScreen : Screens("add_goal_main")
    object AddWeeklyGoalScreen : Screens("add_goal_weekly")
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Meals : BottomNavItem("meals", Icons.Default.Fastfood, "Meals")
    object Goals : BottomNavItem("goals", Icons.Default.Adjust, "Goals")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}
