package com.example.guaumiauapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel // <-- CAMBIO IMPORTANTE
import androidx.lifecycle.viewModelScope
import com.example.guaumiauapp.data.Pet
import com.example.guaumiauapp.data.User
import com.example.guaumiauapp.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

// --- Data classes (se mantienen igual) ---
data class PetState(
    val id: Int,
    val name: String = "",
    val type: String = "",
    val nameError: String? = null,
    val typeError: String? = null
)

data class RegisterFormState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirmPass: String = "",
    val pets: List<PetState> = emptyList(),
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmPassError: String? = null,
    val registrationSuccess: Boolean = false // Estado para notificar a la Vista
)

// CAMBIO: Hereda de AndroidViewModel
class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)
    private val petIdCounter = AtomicInteger()

    private val _uiState = MutableStateFlow(RegisterFormState())
    val uiState: StateFlow<RegisterFormState> = _uiState.asStateFlow()

    val petTypes = listOf("Gato", "Perro", "Ave", "Otro")

    // --- Funciones de cambio (iguales) ---
    fun onFullNameChange(name: String) { _uiState.update { it.copy(fullName = name, fullNameError = null) } }
    fun onEmailChange(email: String) { _uiState.update { it.copy(email = email, emailError = null) } }
    fun onPhoneChange(phone: String) { if (phone.all { it.isDigit() }) { _uiState.update { it.copy(phone = phone) } } }
    fun onPasswordChange(pass: String) { _uiState.update { it.copy(pass = pass, passError = null) } }
    fun onConfirmPasswordChange(confirmPass: String) { _uiState.update { it.copy(confirmPass = confirmPass, confirmPassError = null) } }
    fun onPetNameChange(petId: Int, name: String) { _uiState.update { currentState -> currentState.copy(pets = currentState.pets.map { if (it.id == petId) it.copy(name = name, nameError = null) else it }) } }
    fun onPetTypeChange(petId: Int, type: String) { _uiState.update { currentState -> currentState.copy(pets = currentState.pets.map { if (it.id == petId) it.copy(type = type, typeError = null) else it }) } }
    fun addPet() { val newPet = PetState(id = petIdCounter.getAndIncrement()); _uiState.update { it.copy(pets = it.pets + newPet) } }
    fun removePet(petId: Int) { _uiState.update { currentState -> currentState.copy(pets = currentState.pets.filterNot { it.id == petId }) } }

    /**
     * Lanza la validación y el registro.
     * El resultado se publicará en el _uiState.
     */
    fun validateAndRegister() {
        // CAMBIO: Toda la validación y registro ocurre en una coroutine
        viewModelScope.launch {
            val state = _uiState.value
            // CAMBIO: Verificamos el email en la BD (función 'suspend')
            val isEmailRegistered = repository.isEmailRegistered(state.email)

            // --- Validaciones (igual que antes, pero con el chequeo de BD) ---
            val nameErr = if (state.fullName.isBlank()) "No puede estar vacío"
            else if (!state.fullName.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚüÜ ]+$".toRegex())) "Solo letras y espacios"
            else if (state.fullName.length > 50) "Máximo 50 caracteres"
            else null

            val emailErr = if (state.email.isBlank()) "No puede estar vacío"
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) "Formato de correo inválido"
            else if (!state.email.endsWith("@duoc.cl")) "Solo se aceptan correos @duoc.cl"
            else if (isEmailRegistered) "Este correo ya está registrado" // <-- Validación de BD
            else null

            val passErr = if (state.pass.length < 8) "Mínimo 8 caracteres"
            // ... (resto de validaciones de contraseña)
            else if (!state.pass.any { it.isUpperCase() }) "Debe contener al menos una mayúscula"
            else if (!state.pass.any { it.isLowerCase() }) "Debe contener al menos una minúscula"
            else if (!state.pass.any { it.isDigit() }) "Debe contener al menos un número"
            else if (!state.pass.matches(".*[@#\\$%].*".toRegex())) "Debe contener un carácter especial (@#$% )"
            else null

            val confirmPassErr = if (state.pass != state.confirmPass) "Las contraseñas no coinciden" else null

            val updatedPets = state.pets.map { pet ->
                val petNameError = if (pet.name.isBlank()) "Obligatorio" else if (pet.name.length > 50) "Máximo 50 caracteres" else null
                val petTypeError = if (pet.type.isBlank()) "Obligatorio" else null
                pet.copy(nameError = petNameError, typeError = petTypeError)
            }
            val petsAreValid = updatedPets.all { it.nameError == null && it.typeError == null }

            val isValid = listOf(nameErr, emailErr, passErr, confirmPassErr).all { it == null } && petsAreValid

            if (isValid) {
                // --- Guardar en la Base de Datos ---
                val newUser = User(
                    fullName = state.fullName,
                    email = state.email,
                    phone = state.phone.takeIf { it.isNotBlank() }, // Guarda null si está vacío
                    pass = state.pass,
                    pets = state.pets.map { Pet(name = it.name, type = it.type) }
                )
                repository.addUser(newUser) // <-- CAMBIO: Llama a la BD
                _uiState.update { it.copy(registrationSuccess = true) }
            } else {
                // --- Actualizar UI con errores ---
                _uiState.update {
                    it.copy(
                        fullNameError = nameErr,
                        emailError = emailErr,
                        passError = passErr,
                        confirmPassError = confirmPassErr,
                        pets = updatedPets,
                        registrationSuccess = false
                    )
                }
            }
        }
    }

    // Función para resetear el estado de éxito después de navegar
    fun onRegistrationHandled() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }
}