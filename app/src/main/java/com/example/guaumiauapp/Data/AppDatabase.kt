package com.example.guaumiauapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class], version = 1)
@TypeConverters(DatabaseConverters::class) // Le decimos a Room que use nuestro traductor
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        // Volatile asegura que la instancia sea siempre la más actualizada
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Singleton para obtener la base de datos
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "guau_miau_database" // Nombre del archivo de la base de datos
                )
                    .fallbackToDestructiveMigration() // Si cambiamos la versión, borra los datos antiguos
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}