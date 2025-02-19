package com.fiteats.app.ui.navigation


sealed class Screens(val route: String) {
    object SplashScreen : Screens("splash")
    object LoginScreen : Screens("login")
    object RegisterScreen : Screens("register")
    object MainScreen : Screens("main")
}
