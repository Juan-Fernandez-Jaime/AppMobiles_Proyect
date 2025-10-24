package com.example.guaumiauapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.guaumiauapp.navigation.AppRoutes
import com.example.guaumiauapp.ui.theme.GuaumiauappTheme
import com.example.guaumiauapp.views.LoginScreen
import com.example.guaumiauapp.views.RegisterScreen
import com.example.guaumiauapp.views.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GuaumiauappTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    // Configuración del gestor de navegación
                    NavHost(
                        navController = navController,
                        startDestination = AppRoutes.LoginScreen.route
                    ) {
                        composable(AppRoutes.LoginScreen.route) {
                            LoginScreen(navController)
                        }
                        composable(AppRoutes.RegisterScreen.route) {
                            RegisterScreen(navController)
                        }
                        composable(AppRoutes.MainScreen.route) {
                            MainScreen(navController)
                        }


                    }
                }
            }
        }
    }
}