package com.example.cardgame.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CategoryToggleDialog(
    categories: List<String>,
    initiallyEnabled: Set<String>,
    onStart: (Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val checked = remember {
        mutableStateMapOf<String, Boolean>().apply {
            categories.forEach { put(it, it in initiallyEnabled) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose categories") },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                items(categories) { category ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked[category] == true,
                            onCheckedChange = { checked[category] = it }
                        )
                        Text(category)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val enabled = checked.filterValues { it }.keys.toSet()
                    onStart(enabled)
                },
                enabled = checked.values.any { it }
            ) {
                Text("Start")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}