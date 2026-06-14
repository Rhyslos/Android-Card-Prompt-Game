package com.example.cardgame.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ShortfallDialog(
    shortfalls: List<String>,
    onPlayAnyway: () -> Unit,
    onGoBack: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onGoBack,
        title = { Text("Not enough cards") },
        text = {
            Text(
                "These categories ran short of unused cards:\n\n" +
                        shortfalls.joinToString("\n") { "- $it" } +
                        "\n\nThe deck was filled to 50 using other categories. Play anyway, or go back?"
            )
        },
        confirmButton = {
            Button(onClick = onPlayAnyway) { Text("Play anyway") }
        },
        dismissButton = {
            TextButton(onClick = onGoBack) { Text("Go back") }
        }
    )
}