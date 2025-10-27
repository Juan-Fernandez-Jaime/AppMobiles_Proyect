package com.example.guaumiauapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel // <-- CAMBIO IMPORTANTE
import androidx.lifecycle.viewModelScope
import com.example.guaumiauapp.data.UserRepository // <-- AHORA EXISTE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
// import kotlinx.coroutines.delay // Ya no es necesario

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val isLoading: Boolean = false
)

// CAMBIO: Hereda de AndroidViewModel para tener 'application' (contexto)
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // Inicializa el repositorio real
    private val repository = UserRepository(application)

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
            // delay(1000) // Opcional si quieres que la carga dure más
            val state = _uiState.value

            // CAMBIO: Llama al repositorio real (que es 'suspend')
            val user = repository.findUserByCredentials(state.email, state.pass)

            if (user != null) {
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            } else {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Usuario o contraseña incorrectos.",
                    loginSuccess = false
                )}
            }
        }
    }

    fun onLoginHandled() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}