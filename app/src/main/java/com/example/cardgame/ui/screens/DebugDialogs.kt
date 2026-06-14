package com.example.cardgame.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cardgame.data.Card

// Debug Menu

@Composable
fun DebugMenuDialog(
    showTapZones: Boolean,
    onToggleTapZones: (Boolean) -> Unit,
    onViewCards: () -> Unit,
    onWipeCounter: () -> Unit,
    onWipeDb: () -> Unit,
    onClearPlayerCache: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Debug Menu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                // Tap Zone Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Tap Zones")
                    Switch(
                        checked = showTapZones,
                        onCheckedChange = onToggleTapZones
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Button(onClick = onViewCards, modifier = Modifier.fillMaxWidth()) {
                    Text("View all cards")
                }
                Button(onClick = onClearPlayerCache, modifier = Modifier.fillMaxWidth()) {
                    Text("Wipe Saved Players")
                }
                Button(onClick = onWipeCounter, modifier = Modifier.fillMaxWidth()) {
                    Text("Wipe Card Counter")
                }
                Button(onClick = onWipeDb, modifier = Modifier.fillMaxWidth()) {
                    Text("Wipe Database")
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
                        Text("Plays: ${card.useCount}  |  Used: ${card.isUsed}", color = Color.Gray)
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

// Wipe Confirmations

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

@Composable
fun WipeDbConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wipe Database?") },
        text = { Text("This completely deletes all cards from your device. You will need an internet connection to sync them again.") },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Wipe DB") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}