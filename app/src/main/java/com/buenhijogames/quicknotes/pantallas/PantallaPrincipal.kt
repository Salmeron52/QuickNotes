package com.buenhijogames.quicknotes.pantallas

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buenhijogames.quicknotes.componentes.MiTextField
import com.buenhijogames.quicknotes.model.Tarea
import com.buenhijogames.quicknotes.viewmodels.TareaViewModel
import kotlin.math.roundToInt

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    modifier: Modifier = Modifier,
    tareas: List<Tarea>,
    onUpsertTarea: (Tarea) -> Unit,
    onDeleteTarea: (Tarea) -> Unit,
    onReorder: (Int, Int) -> Unit,
    appTitle: String,
    onTitleUpdated: (String) -> Unit,
    viewModel: TareaViewModel,
) {
    var isEditingTitle by remember { mutableStateOf(false) }
    var editedTitle by remember {
        mutableStateOf(
            TextFieldValue(
                appTitle,
                TextRange(appTitle.length) // para que el cursor esté al final del texto
            )
        )
    }
    val titleFocusRequester = remember { FocusRequester() }
    val titleKeyboardController = LocalSoftwareKeyboardController.current

    var showNewTaskField by remember { mutableStateOf(false) }
    var newTaskText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val snackbarHostState = remember { SnackbarHostState() }

    var currentOffset by remember { mutableFloatStateOf(0f) }
    val itemHeights = remember { mutableMapOf<Int, Int>() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                title = {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isEditingTitle) {
                            LaunchedEffect(Unit) {
                                titleFocusRequester.requestFocus()
                                titleKeyboardController?.show()
                            }

                            TextField(
                                value = editedTitle,
                                onValueChange = { editedTitle = it },
                                textStyle = TextStyle(fontSize = 26.sp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done,
                                    capitalization = KeyboardCapitalization.Sentences
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        isEditingTitle = false
                                        onTitleUpdated(editedTitle.text)
                                        titleKeyboardController?.hide()
                                    }
                                ),
                                modifier = Modifier
                                    .focusRequester(titleFocusRequester)
                            )
                        } else {
                            Text(
                                text = appTitle.take(15),
                                fontSize = 36.sp,
                                modifier = Modifier.clickable {
                                    isEditingTitle = true
                                    editedTitle =
                                        TextFieldValue(appTitle, TextRange(appTitle.length))
                                }
                            )
                        }

                        Text("quickNotes by buenhijoGames", fontSize = 10.sp)
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showNewTaskField = true
                    newTaskText = ""
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(Icons.Rounded.Add, "Añadir tarea")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo de texto para nueva tarea (solo visible cuando se presiona el FAB)
            if (showNewTaskField) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }

                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .sizeIn(minHeight = 64.dp)
                        .fillMaxWidth()
                        .clickable { /* acción */ },
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        MiTextField(
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            textStyle = TextStyle(fontSize = 20.sp),
                            texto = newTaskText,
                            placeholder = "",
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            maxLines = 3,
                            onValueChange = { newTaskText = it },
                            // Añadimos estas opciones para manejar el teclado
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                capitalization = KeyboardCapitalization.Sentences
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (newTaskText.isNotEmpty()) {
                                        onUpsertTarea(Tarea(tarea = newTaskText))
                                        showNewTaskField = false
                                        keyboardController?.hide()
                                    } else {
                                        showNewTaskField = false
                                        keyboardController?.hide()
                                    }
                                }
                            )
                        )

                        IconButton(
                            onClick = {
                                if (newTaskText.isNotEmpty()) {
                                    onUpsertTarea(Tarea(tarea = newTaskText))
                                    showNewTaskField = false
                                    keyboardController?.hide()
                                } else {
                                    showNewTaskField = false
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Guardar tarea",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            LazyColumn {
                itemsIndexed(tareas) { index, tarea ->
                    val offset by derivedStateOf {
                        if (viewModel.currentIndex == index) currentOffset else 0f
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(0, offset.roundToInt()) }
                            .alpha(
                                if (viewModel.isDragging && viewModel.currentIndex != index) 0.6f
                                else 1f
                            )
                            .onGloballyPositioned {
                                itemHeights[index] = it.size.height
                            }
                            .pointerInput(Unit) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = {
                                        viewModel.currentIndex = index
                                        currentOffset = 0f
                                        viewModel.isDragging = true
                                    },
                                    onDrag = { _, dragAmount ->
                                        currentOffset += dragAmount.y
                                        val itemHeight = itemHeights[viewModel.currentIndex] ?: 0

                                        if (itemHeight > 0) {
                                            val delta = (currentOffset / itemHeight).toInt()
                                            val newIndex = (viewModel.currentIndex + delta)
                                                .coerceIn(0, tareas.lastIndex)

                                            if (newIndex != viewModel.currentIndex) {
                                                val actualDelta = newIndex - viewModel.currentIndex
                                                currentOffset -= actualDelta * itemHeight
                                                onReorder(viewModel.currentIndex, newIndex)
                                                viewModel.currentIndex = newIndex
                                            }
                                        }
                                    },
                                    onDragEnd = {
                                        viewModel.currentIndex = -1
                                        currentOffset = 0f
                                        viewModel.isDragging = false
                                    }
                                )
                            }
                    ) {
                        var deletedTask: Tarea? by remember { mutableStateOf(null) }

                        ItemTarea(
                            tarea = tarea,
                            onDeleteTarea = { taskToDelete ->
                                deletedTask = taskToDelete
                                onDeleteTarea(taskToDelete)  // Usamos el parámetro recibido
                            },
                            onUpsertTarea = { updatedTarea -> onUpsertTarea(updatedTarea) },
                            viewModel
                        )

                        // Snackbar logic
                        deletedTask?.let { task ->
                            LaunchedEffect(task) {
                                val result = snackbarHostState.showSnackbar(
                                    message = "Tarea '${task.tarea.take(15)}...' eliminada",
                                    actionLabel = "DESHACER",
                                    duration = SnackbarDuration.Short
                                )

                                if (result == SnackbarResult.ActionPerformed) {
                                    onUpsertTarea(task)  // Usamos el parámetro onUpsertTarea
                                }
                                deletedTask = null
                            }
                        }
                    }
                }

            }
        }


    }
}


@Composable
fun ItemTarea(
    tarea: Tarea,
    onDeleteTarea: (Tarea) -> Unit,
    onUpsertTarea: (Tarea) -> Unit = {},
    viewModel: TareaViewModel,
) {

    var mostraIconoBorrar by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember {
        mutableStateOf(
            TextFieldValue(
                tarea.tarea,
                TextRange(tarea.tarea.length) // para que el cursor esté al final del texto
            )
        )
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    // Actualiza el texto cuando la tarea cambia externamente
    LaunchedEffect(tarea) {
        editedText = TextFieldValue(tarea.tarea, TextRange(tarea.tarea.length))
        isEditing = false
        mostraIconoBorrar = false
    }

    // Diálogo de confirmación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar tarea") },
            text = {
                Text("¿Eliminar tarea: '${tarea.tarea.take(10)}${if (tarea.tarea.length > 10) "..." else ""}'?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTarea(tarea)
                        showDeleteDialog = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        mostraIconoBorrar = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .sizeIn(minHeight = 64.dp)
            .fillMaxWidth()
            .clickable {
                mostraIconoBorrar = true
                isEditing = true
            },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Row {
                    if (isEditing) {

                        LaunchedEffect(isEditing) {
                            if (isEditing) {
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }
                        }

                        TextField(
                            value = editedText,
                            onValueChange = { editedText = it },
                            textStyle = TextStyle(fontSize = 20.sp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            maxLines = 3,
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done,
                                capitalization = KeyboardCapitalization.Sentences
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    val updatedTarea = tarea.copy(tarea = editedText.text)
                                    onUpsertTarea(updatedTarea)
                                    isEditing = false
                                    mostraIconoBorrar = false
                                    keyboardController?.hide()
                                }
                            ),
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = tarea.tarea,
                            maxLines = 1,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clickable {
                                    isEditing = true
                                    mostraIconoBorrar = true
                                    editedText = TextFieldValue(
                                        text = tarea.tarea,
                                        selection = TextRange(tarea.tarea.length)
                                    )
                                }
                                .fillMaxWidth()
                        )
                    }
                }

            }

            Column(modifier = Modifier.weight(.1f)) {

                if (mostraIconoBorrar) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Rounded.Delete,
                            "Eliminar tarea"
                        )
                    }
                }

                if (isEditing) {
                    IconButton(
                        onClick = {
                            val updatedTarea = tarea.copy(tarea = editedText.text)
                            onUpsertTarea(updatedTarea)
                            isEditing = false
                            mostraIconoBorrar = false
                            keyboardController?.hide()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Guardar cambios"
                        )
                    }
                }
            }


        }
    }
}
