package com.fiteats.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fiteats.app.ui.navigation.BottomNavItem
import com.fiteats.app.ui.navigation.Screens
import com.fiteats.app.ui.screens.main.HomeScreen
import com.fiteats.app.ui.screens.main.MealScreen
import com.fiteats.app.ui.screens.main.MyGoalsScreen
import com.fiteats.app.ui.screens.main.UserProfileScreen
import com.fiteats.app.utils.GsonUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(mainNavController: NavController) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Meals.route) { MealScreen() }
            composable(BottomNavItem.Goals.route) {
                MyGoalsScreen(
                    onAddWeeklyGoal = { mainGoal ->
                        val route = Screens.AddWeeklyGoalScreen.route +
                                "/" +
                                GsonUtil.gson.toJson(mainGoal)
                        mainNavController.navigate(
                            route = route
                        )
                    },
                    onAddMainGoal = { mainNavController.navigate(route = Screens.AddMainGoalScreen.route) }
                )
            }
            composable(BottomNavItem.Profile.route) {
                UserProfileScreen {
                    mainNavController.navigate(route = Screens.SplashScreen.route) {
                        mainNavController.currentBackStackEntry?.let {
                            popUpTo(it.destination.id) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items =
        listOf(BottomNavItem.Home, BottomNavItem.Meals, BottomNavItem.Goals, BottomNavItem.Profile)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route, // Highlight based on current route
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(rememberNavController())
}