package com.example.cardgame.data

// Game Mode Model

data class GameMode(
    val displayName: String,
    val category: String
)

// Game Mode Registry

val gameModes = listOf(
    GameMode(displayName = "The Dictator", category = "The Dictator"),
    GameMode(displayName = "Two Truths & A Lie", category = "Two Truths & A Lie"),
    GameMode(displayName = "Would I Lie To You", category = "Would I Lie To You")
)