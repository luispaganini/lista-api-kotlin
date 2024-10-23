package com.example.api_list.database

import android.content.Context
import androidx.room.Room


object DatabaseBuilder {
    private var instance: AppDatabase? = null

    fun getInstance(context: Context? = null): AppDatabase {
        return instance ?: synchronized(this) {
            if (context == null) {
                throw IllegalStateException(
                    "DatabaseBuilder.getInstance(context) deve ser " +
                            "inicializado antes de usar"
                )
            }
            val newInstance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                .addMigrations(DatabaseMigrations.MIGRATION_1_TO_2)
                .build()

            instance = newInstance
            newInstance
        }
    }
}