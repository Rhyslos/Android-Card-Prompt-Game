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
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val minDeckSize = 5
private const val maxDeckSize = 100
private const val maxWeight = 10

@Composable
fun CustomGameDialog(
    categories: List<String>,
    initialWeights: Map<String, Int>,
    initialDeckSize: Int,
    onStart: (deckSize: Int, weights: Map<String, Int>) -> Unit,
    onDismiss: () -> Unit
) {
    var deckSize by remember { mutableIntStateOf(initialDeckSize) }
    val weights = remember {
        mutableStateMapOf<String, Int>().apply {
            categories.forEach { put(it, initialWeights[it] ?: 3) }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Custom Game") },
        text = {
            LazyColumn(modifier = Modifier.heightIn(max = 460.dp)) {

                item {
                    Text("Deck size: $deckSize")
                    Slider(
                        value = deckSize.toFloat(),
                        onValueChange = { deckSize = it.toInt() },
                        valueRange = minDeckSize.toFloat()..maxDeckSize.toFloat()
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text("Category weights (0 = off)")
                }

                items(categories) { category ->
                    val w = weights[category] ?: 0
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text("$category: $w")
                        Slider(
                            value = w.toFloat(),
                            onValueChange = { weights[category] = it.toInt() },
                            valueRange = 0f..maxWeight.toFloat(),
                            steps = maxWeight - 1
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onStart(deckSize, weights.toMap())
                },
                enabled = weights.values.any { it > 0 }
            ) {
                Text("Start")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}