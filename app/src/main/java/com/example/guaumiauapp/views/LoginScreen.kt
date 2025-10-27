// Reemplaza todo el archivo views/LoginScreen.kt

package com.example.guaumiauapp.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.guaumiauapp.navigation.AppRoutes
import com.example.guaumiauapp.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    vm: LoginViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            navController.navigate(AppRoutes.MainScreen.route) {
                popUpTo(AppRoutes.LoginScreen.route) { inclusive = true }
            }
            vm.onLoginHandled()
        }
    }

    // 1. Envolvemos todo en un Box
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- Interfaz de Login ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("GUAU&MIAU", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(48.dp))

            uiState.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            OutlinedTextField(
                value = uiState.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading // 2. Deshabilitar si está cargando
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.pass,
                onValueChange = vm::onPasswordChange,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading // 2. Deshabilitar si está cargando
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    vm.validateLogin()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading // 2. Deshabilitar si está cargando
            ) {
                Text("INICIAR SESIÓN")
            }

            TextButton(onClick = { navController.navigate(AppRoutes.RegisterScreen.route) }) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }

        // --- 3. Capa de Carga (Overlay) ---
        AnimatedVisibility(
            visible = uiState.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Iniciando sesión...", color = Color.White)
                }
            }
        }
    }
}