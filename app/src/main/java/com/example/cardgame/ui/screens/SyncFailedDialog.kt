package com.example.cardgame.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@Composable
fun SyncFailedDialog(
    hasExistingCards: Boolean,
    onRetry: () -> Unit,
    onPlayWithOldDb: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(3) }

    LaunchedEffect(Unit) {
        countdown = 3
        while (countdown > 0) {
            delay(1000)
            countdown -= 1
        }
    }

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Sync failed") },
        text = { Text("Couldn't reach the card sheet.") },
        confirmButton = {
            Button(
                onClick = onRetry,
                enabled = countdown == 0
            ) {
                Text(if (countdown == 0) "Retry" else "Retry ($countdown)")
            }
        },
        dismissButton = {
            if (hasExistingCards) {
                TextButton(onClick = onPlayWithOldDb) {
                    Text("Play with old cards")
                }
            }
        }
    )
}