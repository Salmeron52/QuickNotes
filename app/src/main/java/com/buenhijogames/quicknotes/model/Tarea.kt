package com.buenhijogames.quicknotes.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class Tarea (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "tarea")
    val tarea: String = "",

    @ColumnInfo(name = "orden")
    val orden: Int = 0
)
