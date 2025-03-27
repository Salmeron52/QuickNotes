package com.buenhijogames.quicknotes.di

import android.content.Context
import androidx.annotation.Keep
import androidx.room.Room
import com.buenhijogames.quicknotes.room.TareaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.buenhijogames.quicknotes.room.TareaDatabase

@Keep
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideTareaDao(tareaDatabase: TareaDatabase): TareaDao {
        return tareaDatabase.tareaDao() //Para usar los metodos de la interfaz TareaDao()
    }

    @Singleton
    @Provides
    fun provideTareaDatabase(@ApplicationContext context: Context): TareaDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = TareaDatabase::class.java,
            name = "tarea_database"
        ).build()
    }
}