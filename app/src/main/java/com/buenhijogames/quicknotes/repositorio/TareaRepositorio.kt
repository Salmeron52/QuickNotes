package com.buenhijogames.quicknotes.repositorio

import com.buenhijogames.quicknotes.model.AppSettings
import com.buenhijogames.quicknotes.model.Tarea
import com.buenhijogames.quicknotes.room.TareaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class TareaRepositorio @Inject constructor(private val tareaDao: TareaDao) {
    fun getAll() = tareaDao.getAll().flowOn(Dispatchers.IO).conflate()
    suspend fun upsert(tarea: Tarea) = tareaDao.upsert(tarea)
    suspend fun delete(tarea: Tarea) = tareaDao.delete(tarea)

    suspend fun updateOrder(id: Long, newOrder: Int) = tareaDao.updateOrder(id, newOrder)

    fun getAppSettings() = tareaDao.getAppSettings().flowOn(Dispatchers.IO).conflate()
    suspend fun upsertAppSettings(settings: AppSettings) = tareaDao.upsertAppSettings(settings)
}