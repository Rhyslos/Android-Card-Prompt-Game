package com.example.cardgame.data

import android.content.Context
import kotlinx.serialization.Serializable

// Data Models

@Serializable
data class RawGameMode( // <--- CHANGED FROM gameMode
    val Target: String,
    val Category: String,
    val Details: String,
    val Timestamp: String,
)

// Parsing Functions

fun HandleJSONParsing(Category: String){

    val keywordText: String = Category

    when(Category){
        "The Dictator" -> {

        }
        "Would I Lie To You" -> {

        }

        "Two Truths & A Lie" -> {

        }
    }
}

// Storage Functions

class PlayerPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    fun saveNames(names: List<String>) {
        prefs.edit().putString("saved_names", names.joinToString(",")).apply()
    }

    fun getNames(): List<String> {
        val rawString = prefs.getString("saved_names", "") ?: ""
        return if (rawString.isEmpty()) emptyList() else rawString.split(",")
    }
}