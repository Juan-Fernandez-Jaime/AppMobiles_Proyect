package com.example.guaumiauapp.navigation

sealed class AppRoutes(val route: String) {
    object LoginScreen : AppRoutes("login_screen")
    object RegisterScreen : AppRoutes("register_screen")
    object MainScreen : AppRoutes("main_screen")
}