package com.buenhijogames.quicknotes.room

import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.RoomDatabase
import com.buenhijogames.quicknotes.model.AppSettings
import com.buenhijogames.quicknotes.model.Tarea

@Keep
@Database(
    entities = [Tarea::class, AppSettings::class],
    version = 3,
    exportSchema = false
)
abstract class TareaDatabase : RoomDatabase() {
    abstract fun tareaDao(): TareaDao
}