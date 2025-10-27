package com.example.guaumiauapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Esta clase simple la usará el TypeConverter
data class Pet(
    val name: String,
    val type: String
)

// Esta es la "Entidad" que se convertirá en una tabla de la base de datos
@Entity(tableName = "users")
data class User(
    val fullName: String,
    @PrimaryKey // El email será el identificador único de la tabla
    val email: String,
    val phone: String?, // El '?' indica que puede ser nulo
    val pass: String,
    val pets: List<Pet> // Room no sabe cómo guardar esto, usaremos un TypeConverter
)