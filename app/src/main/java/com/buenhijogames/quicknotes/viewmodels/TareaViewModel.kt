package com.buenhijogames.quicknotes.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.buenhijogames.quicknotes.model.AppSettings
import com.buenhijogames.quicknotes.model.Tarea
import com.buenhijogames.quicknotes.repositorio.TareaRepositorio
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    private val tareaRepositorio: TareaRepositorio,
) : ViewModel() {

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings = _appSettings.asStateFlow()

    private val _listaDeTareas: MutableStateFlow<List<Tarea>> = MutableStateFlow(emptyList())
    val listaDeTareas = _listaDeTareas.asStateFlow()


    var isDragging by mutableStateOf(false)
    var currentIndex by mutableIntStateOf(-1)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tareaRepositorio.getAll().distinctUntilChanged().collect { it: List<Tarea> ->
                if (it.isEmpty()) {
                    _listaDeTareas.value = emptyList()
                    Log.d("TareaViewModel", "No hay tareas")
                } else {
                    _listaDeTareas.value = it
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            tareaRepositorio.getAppSettings().collect { settings ->
                _appSettings.value = settings ?: AppSettings().also {
                    tareaRepositorio.upsertAppSettings(it)
                }
            }
        }
    }

    fun updateAppTitle(newTitle: String) {
        viewModelScope.launch {
            val currentSettings = _appSettings.value.copy(appTitle = newTitle)
            _appSettings.value = currentSettings
            tareaRepositorio.upsertAppSettings(currentSettings)
        }
    }

    fun reordenarTareas(oldIndex: Int, newIndex: Int) {
        val newList = _listaDeTareas.value.toMutableList()
        val movedItem = newList.removeAt(oldIndex)
        newList.add(newIndex, movedItem)

        newList.forEachIndexed { index, tarea ->
            if (tarea.orden != index) {
                viewModelScope.launch {
                    tareaRepositorio.updateOrder(tarea.id, index)
                }
            }
        }

        _listaDeTareas.value = newList
    }

    fun upsert(tarea: Tarea) = viewModelScope.launch { tareaRepositorio.upsert(tarea) }

    fun delete(tarea: Tarea): Tarea {
        val deletedTask = tarea.copy() // Guardamos una copia
        viewModelScope.launch { tareaRepositorio.delete(tarea) }
        return deletedTask
    }
}