package com.example.cardgame.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cardgame.data.Card

// Debug Menu

@Composable
fun DebugMenuDialog(
    onViewCards: () -> Unit,
    onWipeCounter: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Debug") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onViewCards, modifier = Modifier.fillMaxWidth()) {
                    Text("View all cards")
                }
                Button(onClick = onWipeCounter, modifier = Modifier.fillMaxWidth()) {
                    Text("Wipe card counter")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

// Card List

@Composable
fun DebugCardListDialog(cards: List<Card>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("All cards (${cards.size})") },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(cards) { card ->
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Text("ID: ${card.id}  |  ${card.category}")
                        Text("Target: ${card.target}")
                        Text(card.details)
                    }
                    HorizontalDivider()
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

// Wipe Confirmation

@Composable
fun WipeConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wipe card counter?") },
        text = { Text("This resets every card's used status and play count to zero.") },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Wipe") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}