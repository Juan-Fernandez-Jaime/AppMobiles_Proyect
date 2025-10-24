package com.example.guaumiauapp.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.guaumiauapp.viewmodels.PetState

/**
 * Componente de UI reutilizable para el formulario de una única mascota.
 * Es "tonto", solo recibe datos y notifica eventos hacia afuera (al ViewModel).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormComponent(
    petState: PetState,
    petTypes: List<String>,
    onNameChange: (String) -> Unit,
    onTypeChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    // Estado para controlar si el menú desplegable está abierto o cerrado
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Campo de texto para el nombre de la mascota
            OutlinedTextField(
                value = petState.name,
                onValueChange = onNameChange,
                label = { Text("Nombre de la mascota *") },
                isError = petState.name!= null,
                supportingText = { petState.name.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            // Menú desplegable (Dropdown) para el tipo de mascota
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = petState.type,
                    onValueChange = {}, // No se cambia escribiendo
                    readOnly = true,
                    label = { Text("Tipo de mascota *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    isError = petState.type != null,
                    supportingText = { petState.type.let { Text(it) } },
                    modifier = Modifier
                        .menuAnchor() // Vincula este campo al menú
                        .fillMaxWidth()
                )
                // Contenido del menú que aparece cuando se expande
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    petTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                onTypeChange(type) // Llama a la función del ViewModel
                                isDropdownExpanded = false // Cierra el menú
                            }
                        )
                    }
                }
            }

            // Botón para eliminar esta mascota
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = onRemove,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}