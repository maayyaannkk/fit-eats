package com.fiteats.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.fiteats.app.R
import com.fiteats.app.ui.navigation.Screens
import com.fiteats.app.utils.UserUtils

@Composable
fun MainScreen(navHostController: NavHostController) {
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
        Text(
            text = "Hello, welcome to app ${UserUtils.getUser(context)?.name}",
            style = MaterialTheme.typography.bodySmall
        )
        Button(onClick = {
            UserUtils.clearUser(context)
            navHostController.navigate(route = Screens.SplashScreen.route) {
                navHostController.currentBackStackEntry?.let {
                    popUpTo(it.destination.id) {
                        inclusive = true
                    }
                }
            }
        }) { Text(text = "logout") }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(navHostController = rememberNavController())
}