package com.buenhijogames.quicknotes.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.buenhijogames.quicknotes.model.AppSettings
import com.buenhijogames.quicknotes.model.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {
    @Query("SELECT * FROM tareas ORDER BY orden ASC")
    fun getAll(): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    fun getById(id: Long): Flow<Tarea>

    @Upsert
    suspend fun upsert(tarea: Tarea)

    @Delete
    suspend fun delete(tarea: Tarea)

    @Query("UPDATE tareas SET orden = :newOrder WHERE id = :id")
    suspend fun updateOrder(id: Long, newOrder: Int)

    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getAppSettings(): Flow<AppSettings>

    @Upsert
    suspend fun upsertAppSettings(settings: AppSettings)
}