package com.example.guaumiauapp.views

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.guaumiauapp.navigation.AppRoutes

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "¡Bienvenido a GUAU&MIAU!",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(onClick = {
            // Esta navegación especial limpia el historial para que el usuario no pueda
            // volver al login con el botón de "Atrás".
            navController.navigate(AppRoutes.LoginScreen.route) {
                popUpTo(AppRoutes.MainScreen.route) {
                    inclusive = true
                }
            }
        }) {
            Text("Cerrar Sesión")
        }
    }
}