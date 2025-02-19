package com.fiteats.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fiteats.app.ui.navigation.AppNavigation
import com.fiteats.app.ui.theme.FitEatsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitEatsTheme {
                AppNavigation()
            }
        }
    }
}