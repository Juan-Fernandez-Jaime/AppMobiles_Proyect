package com.example.guaumiauapp.data

import android.content.Context

/**
 * Repositorio que usa Room (SQLite) como fuente de datos.
 * Necesita el Context para poder instanciar la base de datos.
 */
class UserRepository(context: Context) {

    // Obtenemos el DAO desde nuestra base de datos Singleton
    private val userDao = AppDatabase.getDatabase(context).userDao()

    /**
     * Añade un nuevo usuario a la base de datos.
     * Esta es una "suspend function" porque las operaciones de BD
     * no pueden correr en el hilo principal.
     */
    suspend fun addUser(user: User) {
        userDao.insertUser(user)
    }

    /**
     * Busca un usuario por sus credenciales en la base de datos.
     */
    suspend fun findUserByCredentials(email: String, pass: String): User? {
        return userDao.findUserByCredentials(email, pass)
    }

    /**
     * Verifica si un email ya está registrado en la base de datos.
     */
    suspend fun isEmailRegistered(email: String): Boolean {
        return userDao.findUserByEmail(email) != null
    }
}