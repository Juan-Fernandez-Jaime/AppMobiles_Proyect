package com.example.guaumiauapp.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicInteger

// --- MODELOS DE ESTADO (DATA CLASSES) ---

/**
 * Representa el estado de una mascota individual en el formulario.
 * Usamos un ID único para poder identificarla y eliminarla correctamente.
 */
data class PetState(
    val id: Int,
    val name: String = "",
    val type: String = "",
    val nameError: String? = null,
    val typeError: String? = null
)

/**
 * Representa el estado completo del formulario de registro.
 * CORRECCIÓN: Ahora incluye las variables para los mensajes de error.
 */
data class RegisterFormState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val pass: String = "",
    val confirmPass: String = "",
    val pets: List<PetState> = emptyList(),
    // --- VARIABLES DE ERROR QUE FALTABAN ---
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passError: String? = null,
    val confirmPassError: String? = null
)

/**
 * ViewModel para la pantalla de registro.
 * Contiene toda la lógica de negocio, validaciones y manejo de estado.
 */
class RegisterViewModel : ViewModel() {

    // Generador de IDs únicos para las mascotas
    private val petIdCounter = AtomicInteger()

    // Flujo de estado privado que contiene el estado actual del formulario
    private val _uiState = MutableStateFlow(RegisterFormState())
    // Exposición pública y de solo lectura del estado para la UI
    val uiState: StateFlow<RegisterFormState> = _uiState.asStateFlow()

    val petTypes = listOf("Gato", "Perro", "Ave", "Otro")

    // --- MANEJADORES DE CAMBIOS EN LA UI ---

    fun onFullNameChange(name: String) {
        // Al cambiar el texto, limpiamos el error
        _uiState.update { it.copy(fullName = name, fullNameError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onPhoneChange(phone: String) {
        // Solo permite números
        if (phone.all { it.isDigit() }) {
            _uiState.update { it.copy(phone = phone) }
        }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(pass = pass, passError = null) }
    }

    fun onConfirmPasswordChange(confirmPass: String) {
        _uiState.update { it.copy(confirmPass = confirmPass, confirmPassError = null) }
    }

    fun onPetNameChange(petId: Int, name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                pets = currentState.pets.map {
                    if (it.id == petId) it.copy(name = name, nameError = null) else it
                }
            )
        }
    }

    fun onPetTypeChange(petId: Int, type: String) {
        _uiState.update { currentState ->
            currentState.copy(
                pets = currentState.pets.map {
                    if (it.id == petId) it.copy(type = type, typeError = null) else it
                }
            )
        }
    }

    // --- LÓGICA DE NEGOCIO ---

    fun addPet() {
        val newPet = PetState(id = petIdCounter.getAndIncrement())
        _uiState.update { it.copy(pets = it.pets + newPet) }
    }

    fun removePet(petId: Int) {
        _uiState.update { currentState ->
            currentState.copy(pets = currentState.pets.filterNot { it.id == petId })
        }
    }

    /**
     * Valida todos los campos del formulario y actualiza el estado con los errores.
     * @return `true` si el formulario es válido, `false` en caso contrario.
     */
    fun validate(): Boolean {
        val state = _uiState.value

        // Validaciones de usuario
        val nameErr = if (state.fullName.isBlank()) "No puede estar vacío"
        else if (!state.fullName.matches("^[a-zA-ZñÑáéíóúÁÉÍÓÚüÜ ]+$".toRegex())) "Solo letras y espacios"
        else if (state.fullName.length > 50) "Máximo 50 caracteres"
        else null

        val emailErr = if (state.email.isBlank()) "No puede estar vacío"
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) "Formato de correo inválido"
        else if (!state.email.endsWith("@duoc.cl")) "Solo se aceptan correos @duoc.cl"
        else null

        val passErr = if (state.pass.length < 8) "Mínimo 8 caracteres"
        else if (!state.pass.any { it.isUpperCase() }) "Debe contener al menos una mayúscula"
        else if (!state.pass.any { it.isLowerCase() }) "Debe contener al menos una minúscula"
        else if (!state.pass.any { it.isDigit() }) "Debe contener al menos un número"
        else if (!state.pass.matches(".*[@#\\$%].*".toRegex())) "Debe contener un carácter especial (@#$% )"
        else null

        val confirmPassErr = if (state.pass != state.confirmPass) "Las contraseñas no coinciden" else null

        // Validaciones de mascotas
        val updatedPets = state.pets.map { pet ->
            val petNameError = if (pet.name.isBlank()) "Obligatorio" else if (pet.name.length > 50) "Máximo 50 caracteres" else null
            val petTypeError = if (pet.type.isBlank()) "Obligatorio" else null
            pet.copy(nameError = petNameError, typeError = petTypeError)
        }
        val petsAreValid = updatedPets.all { it.nameError == null && it.typeError == null }

        // Actualizamos el estado con todos los errores encontrados
        _uiState.update {
            it.copy(
                fullNameError = nameErr,
                emailError = emailErr,
                passError = passErr,
                confirmPassError = confirmPassErr,
                pets = updatedPets
            )
        }

        // El formulario es válido si no hay ningún error
        return listOf(nameErr, emailErr, passErr, confirmPassErr).all { it == null } && petsAreValid
    }
}