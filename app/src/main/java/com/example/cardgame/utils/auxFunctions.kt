package com.example.cardgame.utils

// Player Validation

fun isValidName(name: String): Boolean {
    return name.isNotBlank()
}

fun canAddMorePlayers(currentCount: Int, maxPlayers: Int): Boolean {
    return currentCount < maxPlayers
}