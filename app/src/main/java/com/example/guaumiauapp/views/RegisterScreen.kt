package com.example.guaumiauapp.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.guaumiauapp.components.PetFormComponent
import com.example.guaumiauapp.viewmodels.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    vm: RegisterViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val context = LocalContext.current

    // --- CAMBIO: Observador de éxito ---
    // Se ejecuta cuando uiState.registrationSuccess cambia a true
    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
            navController.popBackStack() // Volver al Login
            vm.onRegistrationHandled() // Resetea el estado
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            // --- CAMPOS DE USUARIO ---
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = uiState.fullName,
                        onValueChange = vm::onFullNameChange,
                        label = { Text("Nombre Completo") },
                        isError = uiState.fullNameError != null,
                        supportingText = { uiState.fullNameError?.let { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = vm::onEmailChange,
                        label = { Text("Correo Electrónico") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = uiState.emailError != null,
                        supportingText = { uiState.emailError?.let { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.phone,
                        onValueChange = vm::onPhoneChange,
                        label = { Text("Teléfono (opcional)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.pass,
                        onValueChange = vm::onPasswordChange,
                        label = { Text("Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = uiState.passError != null,
                        supportingText = { uiState.passError?.let { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = uiState.confirmPass,
                        onValueChange = vm::onConfirmPasswordChange,
                        label = { Text("Confirmar Contraseña") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = uiState.confirmPassError != null,
                        supportingText = { uiState.confirmPassError?.let { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // --- SECCIÓN DE MASCOTAS ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mascotas", style = MaterialTheme.typography.titleLarge)
                    Button(onClick = { vm.addPet() }) {
                        Text("Añadir Mascota")
                    }
                }
            }

            // --- LISTA DINÁMICA DE MASCOTAS ---
            items(uiState.pets, key = { it.id }) { pet ->
                PetFormComponent(
                    petState = pet,
                    petTypes = vm.petTypes,
                    onNameChange = { name -> vm.onPetNameChange(pet.id, name) },
                    onTypeChange = { type -> vm.onPetTypeChange(pet.id, type) },
                    onRemove = { vm.removePet(pet.id) }
                )
            }

            // --- BOTÓN DE REGISTRO FINAL ---
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        // CAMBIO: Ahora solo llama a esta función
                        vm.validateAndRegister()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("REGISTRAR")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}