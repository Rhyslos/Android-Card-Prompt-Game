package com.example.cardgame.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.cardgame.utils.canAddMorePlayers
import com.example.cardgame.utils.isValidName

// Configuration

const val MaxPlayers = 12

// Screen

@Composable
fun PlayerSetupScreen(modifier: Modifier = Modifier, onContinue: () -> Unit = {}) {
    val playerNames = remember { mutableStateListOf<String>() }
    var currentInput by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }

    fun SavePlayerNames() {
        if (isValidName(currentInput)) {
            playerNames.add(currentInput.trim())
            currentInput = ""
            isAdding = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(playerNames) { index, name ->
                PlayerRow(name = name, onRemove = { playerNames.removeAt(index) })
            }

            item {
                if (isAdding) {
                    AddPlayerButton(
                        currentInput = currentInput,
                        onInputChange = { currentInput = it },
                        onConfirm = { SavePlayerNames() },
                        onCancel = {
                            currentInput = ""
                            isAdding = false
                        }
                    )
                } else if (canAddMorePlayers(playerNames.size, MaxPlayers)) {
                    ElevatedButton(
                        onClick = { isAdding = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Add Player")
                    }
                }
            }
        }

        ContinueButton(
            enabled = playerNames.isNotEmpty(),
            onClick = onContinue
        )
    }
}

// Components

@Composable
fun PlayerRow(name: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRemove) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Remove player")
        }
    }
}

@Composable
fun AddPlayerButton(
    currentInput: String,
    onInputChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = currentInput,
            onValueChange = onInputChange,
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 48.dp),
            singleLine = true,
            shape = CircleShape,
            placeholder = { Text("Player name") },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onDone = { onConfirm() }
            )
        )
        IconButton(onClick = onConfirm) {
            Icon(imageVector = Icons.Default.Check, contentDescription = "Save player")
        }
        IconButton(onClick = onCancel) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
        }
    }
}

@Composable
fun ContinueButton(enabled: Boolean, onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Continue")
    }
}