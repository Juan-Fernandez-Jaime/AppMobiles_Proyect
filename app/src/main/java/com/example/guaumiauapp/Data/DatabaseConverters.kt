package com.example.guaumiauapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DatabaseConverters {
    private val gson = Gson()

    // Convierte una lista de mascotas a un string JSON
    @TypeConverter
    fun fromPetList(pets: List<Pet>): String {
        return gson.toJson(pets)
    }

    // Convierte un string JSON de vuelta a una lista de mascotas
    @TypeConverter
    fun toPetList(json: String): List<Pet> {
        val listType = object : TypeToken<List<Pet>>() {}.type
        return gson.fromJson(json, listType)
    }
}