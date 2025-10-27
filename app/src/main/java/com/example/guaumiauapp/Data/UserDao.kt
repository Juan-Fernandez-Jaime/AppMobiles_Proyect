package com.example.guaumiauapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    // Inserta un usuario. Si el email ya existe, aborta.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User) // 'suspend' significa que debe llamarse desde una coroutine

    // Busca un usuario por email y contraseña
    @Query("SELECT * FROM users WHERE email = :email AND pass = :pass LIMIT 1")
    suspend fun findUserByCredentials(email: String, pass: String): User?

    // Busca un usuario solo por email (para verificar duplicados)
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findUserByEmail(email: String): User?
}