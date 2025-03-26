package com.buenhijogames.quicknotes.componentes

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle

@Composable
fun MiTextField(
    modifier: Modifier = Modifier,
    texto: String,
    placeholder: String,
    maxLines: Int = 1,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default, // Valor por defecto
    keyboardActions: KeyboardActions = KeyboardActions(), // Valor por defecto
    colors: TextFieldColors,
    textStyle: TextStyle
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        modifier = modifier,
        value = texto,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        maxLines = maxLines,
        textStyle = textStyle,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        keyboardOptions = keyboardOptions, // Usamos el parámetro recibido
        keyboardActions = keyboardActions // Usamos el parámetro recibido
    )
}

@Composable
fun MiBoton(texto: String, enabled: Boolean = false, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled) {
        Text(texto)
    }
}