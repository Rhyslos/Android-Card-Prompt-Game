package com.example.cardgame.data

import android.content.Context

// Storage Functions

class PlayerPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    fun saveNames(names: List<String>) {
        prefs.edit().putString("saved_names", names.joinToString("\n")).apply()
    }

    fun getNames(): List<String> {
        val rawString = prefs.getString("saved_names", "") ?: ""
        return if (rawString.isEmpty()) emptyList() else rawString.split("\n")
    }

    fun clearNames() {
        prefs.edit().clear().apply()
    }
}