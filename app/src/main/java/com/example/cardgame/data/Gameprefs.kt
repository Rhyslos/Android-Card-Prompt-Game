package com.example.cardgame.data

import android.content.Context

// Game Config Persistence
// Stores per-mode category toggles, and Custom Game's weights + deck size.

class GamePrefs(context: Context) {
    private val prefs = context.getSharedPreferences("game_config_prefs", Context.MODE_PRIVATE)

    // Category toggles, stored per mode key as a comma-free newline list of ENABLED categories.

    fun saveEnabledCategories(modeKey: String, enabled: Set<String>) {
        prefs.edit().putString("toggles_$modeKey", enabled.joinToString("\n")).apply()
    }

    // Returns the saved enabled set, or null if never saved (caller defaults to all-on).

    fun getEnabledCategories(modeKey: String): Set<String>? {
        val raw = prefs.getString("toggles_$modeKey", null) ?: return null
        if (raw.isEmpty()) return emptySet()
        return raw.split("\n").toSet()
    }

    // Custom Game weights, stored as "category=weight" lines.

    fun saveWeights(weights: Map<String, Int>) {
        val encoded = weights.entries.joinToString("\n") { "${it.key}=${it.value}" }
        prefs.edit().putString("custom_weights", encoded).apply()
    }

    fun getWeights(): Map<String, Int>? {
        val raw = prefs.getString("custom_weights", null) ?: return null
        if (raw.isEmpty()) return emptyMap()
        return raw.split("\n").mapNotNull { line ->
            val idx = line.lastIndexOf('=')
            if (idx <= 0) return@mapNotNull null
            val key = line.substring(0, idx)
            val value = line.substring(idx + 1).toIntOrNull() ?: return@mapNotNull null
            key to value
        }.toMap()
    }

    // Custom Game deck size.

    fun saveDeckSize(size: Int) {
        prefs.edit().putInt("custom_deck_size", size).apply()
    }

    fun getDeckSize(): Int {
        return prefs.getInt("custom_deck_size", splashDeckSize)
    }
}