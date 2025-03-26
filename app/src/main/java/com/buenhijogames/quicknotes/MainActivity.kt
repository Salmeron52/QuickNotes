package com.buenhijogames.quicknotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.buenhijogames.quicknotes.model.Tarea
import com.buenhijogames.quicknotes.pantallas.PantallaPrincipal
import com.buenhijogames.quicknotes.ui.theme.QuickNotesTheme
import com.buenhijogames.quicknotes.viewmodels.TareaViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickNotesTheme {
                Scaffold { innerPadding ->
                    val tareaViewModel: TareaViewModel by viewModels()
                    val listaTareas = tareaViewModel.listaDeTareas.collectAsState().value
                    val appSettings by tareaViewModel.appSettings.collectAsState()

                    PantallaPrincipal(
                        modifier = Modifier.padding(innerPadding),
                        tareas = listaTareas,
                        onUpsertTarea = { tarea: Tarea -> tareaViewModel.upsert(tarea) },
                        onDeleteTarea = { tarea: Tarea ->
                            val deletedTask = tareaViewModel.delete(tarea)
                        },
                        onReorder = { oldIndex, newIndex ->
                            tareaViewModel.reordenarTareas(oldIndex, newIndex)
                        },
                        appTitle = appSettings.appTitle,
                        onTitleUpdated = { newTitle ->
                            tareaViewModel.updateAppTitle(newTitle)
                        },
                        tareaViewModel
                    )
                }
            }
        }
    }
}

