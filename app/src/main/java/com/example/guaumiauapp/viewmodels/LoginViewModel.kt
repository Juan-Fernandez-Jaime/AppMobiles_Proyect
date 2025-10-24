package com.example.guaumiauapp.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado de la UI para la pantalla de Login.
 */
data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val errorMessage: String? = null
)

/**
 * ViewModel que maneja la lógica de la pantalla de Login.
 */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(pass = pass, errorMessage = null) }
    }

    /**
     * Valida las credenciales del usuario.
     * En una app real, aquí se consultaría una base de datos o API.
     * Para este ejemplo, usaremos datos fijos.
     * @return `true` si el login es exitoso, `false` si no.
     */
    fun validateLogin(): Boolean {
        if (_uiState.value.email == "usuario@duoc.cl" && _uiState.value.pass == "Duoc123@") {
            _uiState.update { it.copy(errorMessage = null) }
            return true
        } else {
            _uiState.update { it.copy(errorMessage = "Usuario o contraseña incorrectos.") }
            return false
        }
    }
}