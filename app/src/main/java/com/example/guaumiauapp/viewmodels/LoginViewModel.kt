package com.example.guaumiauapp.viewmodels // Asegúrate que el paquete sea correcto

// --- Imports necesarios ---
import android.app.Application // Si usas BD/Repositorio, sino quítalo
import androidx.lifecycle.AndroidViewModel // Si usas BD/Repositorio, sino usa ViewModel
// import androidx.lifecycle.ViewModel // Descomenta si NO usas BD/Repositorio
import androidx.lifecycle.viewModelScope
// import com.example.guaumiauapp.data.UserRepository // Descomenta si usas BD/Repositorio
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado de la UI para la pantalla de Login
data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false, // Para controlar la navegación
    val showSuccessMessage: Boolean = false // <-- **Variable añadida**
)

// --- Elige UNA de las siguientes definiciones de Clase ---

// OPCIÓN 1: Si YA estás usando la Base de Datos (UserRepository)
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = com.example.guaumiauapp.data.UserRepository(application) // Asegúrate que el import sea correcto

// OPCIÓN 2: Si AÚN NO usas Base de Datos (validación fija)
// class LoginViewModel : ViewModel() {
// Fin de las opciones

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(pass = pass, errorMessage = null) }
    }

    fun validateLogin() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            delay(1500) // Simula la carga

            val state = _uiState.value
            var success = false // Variable temporal para saber si fue exitoso

            // --- Elige UNA de las siguientes lógicas de validación ---

            // LÓGICA 1: Si YA usas Base de Datos
            val user = repository.findUserByCredentials(state.email, state.pass)
            success = user != null

            // LÓGICA 2: Si AÚN NO usas Base de Datos
            // success = (state.email == "usuario@duoc.cl" && state.pass == "Duoc123@")
            // Fin de las lógicas

            if (success) {
                _uiState.update { it.copy(
                    isLoading = false,
                    loginSuccess = true, // Indica que la navegación debe ocurrir
                    showSuccessMessage = true // ¡Indica que el mensaje debe mostrarse!
                )}
            } else {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Usuario o contraseña incorrectos.",
                    loginSuccess = false
                )}
            }
        }
    }

    // --- Función añadida para resetear el estado del mensaje ---
    fun onSuccessMessageShown() {
        _uiState.update { it.copy(showSuccessMessage = false) }
    }

    // Función para resetear el estado de navegación
    fun onLoginHandled() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}