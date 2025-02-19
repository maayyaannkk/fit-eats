package com.fiteats.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fiteats.app.ui.navigation.Screens
import kotlinx.coroutines.delay
import com.fiteats.app.R

@Composable
fun SplashScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineMedium
        )

        LaunchedEffect(Unit) {
            delay(250)
            /*if (SharedPrefUtil.isUserLoggedIn(context))
                navHostController.navigate(route = Screens.MainScreen.route) {
                    navHostController.currentBackStackEntry?.let {
                        popUpTo(it.destination.id) {
                            inclusive = true
                        }
                    }
                }
            else*/ navHostController.navigate(route = Screens.LoginScreen.route) {
            navHostController.currentBackStackEntry?.let {
                popUpTo(it.destination.id) {
                    inclusive = true
                }
            }
        }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen(navHostController = rememberNavController())
}