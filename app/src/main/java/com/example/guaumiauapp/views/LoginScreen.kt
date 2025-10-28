package com.example.guaumiauapp.views // Asegúrate que el nombre del paquete sea el correcto para tu proyecto

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.guaumiauapp.navigation.AppRoutes // Asegúrate que la ruta de importación sea correcta
import com.example.guaumiauapp.viewmodels.LoginViewModel // Asegúrate que la ruta de importación sea correcta

@Composable
fun LoginScreen(
    navController: NavController,
    vm: LoginViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current // Necesario para el Toast

    // Efecto para mostrar el Toast de éxito (si está implementado en tu ViewModel)
    LaunchedEffect(uiState.showSuccessMessage) {
        if (uiState.showSuccessMessage) {
            Toast.makeText(context, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()
            vm.onSuccessMessageShown() // Avisa al ViewModel que ya se mostró
        }
    }

    // Efecto para navegar después del éxito
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            navController.navigate(AppRoutes.MainScreen.route) { // Navega a MainScreen
                popUpTo(AppRoutes.LoginScreen.route) { inclusive = true } // Limpia la pila
            }
            vm.onLoginHandled() // Avisa al ViewModel que ya se navegó
        }
    }

    // --- Interfaz de Login ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Asegúrate que el nombre "GUAU&MIAU" sea el correcto para tu app
        Text("GUAU&MIAU", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(48.dp))

        // Muestra mensaje de error si existe
        uiState.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Campo Email
        OutlinedTextField(
            value = uiState.email,
            onValueChange = vm::onEmailChange,
            label = { Text("Correo Electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading // Deshabilitado durante la carga
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = uiState.pass,
            onValueChange = vm::onPasswordChange, // CORREGIDO: 'onValueChange' sin guion
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading // Deshabilitado durante la carga
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- Contenedor para el Botón y el Indicador ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp), // Damos altura fija para que no salte la UI
            contentAlignment = Alignment.Center // Centra el botón o el indicador
        ) {
            // Si NO está cargando, muestra el botón
            if (!uiState.isLoading) {
                Button(
                    onClick = {
                        vm.validateLogin() // Llama a la validación en el ViewModel
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading // Deshabilitado durante la carga
                ) {
                    Text("INICIAR SESIÓN")
                }
            } else {
                // Si SÍ está cargando, muestra el indicador de progreso
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp) // Tamaño del círculo
                )
            }
        } // Fin del Box

        Spacer(modifier = Modifier.height(8.dp)) // Espacio adicional

        // Botón para ir al registro
        // Asegúrate de tener AppRoutes.RegisterScreen definido en tu AppRoutes.kt
        TextButton(
            onClick = { navController.navigate(AppRoutes.RegisterScreen.route) },
            enabled = !uiState.isLoading // Deshabilitado durante la carga
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }

    } // Fin de la Column
}