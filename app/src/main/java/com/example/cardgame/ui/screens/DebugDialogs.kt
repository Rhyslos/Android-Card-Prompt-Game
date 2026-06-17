package com.example.cardgame.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

// Card List (with filters)

@Composable
fun DebugCardListDialog(cards: List<Card>, onDismiss: () -> Unit) {

    var categoryFilter by remember { mutableStateOf<String?>(null) }   // null = all
    var playedFilter by remember { mutableStateOf(0) }                 // 0 = all, 1 = used, 2 = unused
    var sortAscending by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    var showCategoryMenu by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }

    val categories = remember(cards) { cards.map { it.category }.distinct().sorted() }

    val filtered = cards
        .filter { categoryFilter == null || it.category == categoryFilter }
        .filter {
            when (playedFilter) {
                1 -> it.isUsed
                2 -> !it.isUsed
                else -> true
            }
        }
        .filter {
            if (searchQuery.isBlank()) true
            else {
                val q = searchQuery.trim().lowercase()
                it.id.toString().contains(q) ||
                        it.category.lowercase().contains(q) ||
                        it.target.lowercase().contains(q) ||
                        it.details.lowercase().contains(q)
            }
        }
        .let { list ->
            if (sortAscending) list.sortedBy { it.target.lowercase() }
            else list.sortedByDescending { it.target.lowercase() }
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cards (${filtered.size}/${cards.size})") },
        text = {
            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        IconButton(onClick = { showCategoryMenu = true }) {
                            Icon(Icons.Default.List, contentDescription = "Category filter")
                        }
                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All categories") },
                                onClick = { categoryFilter = null; showCategoryMenu = false }
                            )
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = { categoryFilter = cat; showCategoryMenu = false }
                                )
                            }
                        }
                    }

                    IconButton(onClick = { playedFilter = (playedFilter + 1) % 3 }) {
                        when (playedFilter) {
                            1 -> Icon(Icons.Default.Done, contentDescription = "Used only")
                            2 -> Icon(Icons.Default.Clear, contentDescription = "Unused only")
                            else -> Icon(Icons.Default.List, contentDescription = "All cards")
                        }
                    }

                    IconButton(onClick = { sortAscending = !sortAscending }) {
                        if (sortAscending) Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Sort ascending")
                        else Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Sort descending")
                    }

                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }

                    IconButton(onClick = {
                        categoryFilter = null
                        playedFilter = 0
                        sortAscending = true
                        searchQuery = ""
                        showSearch = false
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset filters")
                    }
                }

                if (showSearch) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("Search id, category, target, text") }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                    items(filtered) { card ->
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Text("ID: ${card.id}  |  ${card.category}")
                            Text("Target: ${card.target}")
                            Text(card.details)
                            Text("Plays: ${card.useCount}  |  Used: ${card.isUsed}", color = Color.Gray)
                        }
                        HorizontalDivider()
                    }
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