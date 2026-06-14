package com.example.cardgame.ui.theme

import androidx.compose.ui.graphics.Color

// Category Background Colors
// Similar categories share a color family. Muted but vibrant.

private val spicyRed = Color(0xFFB23A48)     // Dare, Personal Actions, The Dictator
private val socialGreen = Color(0xFF3F8F5B)  // Most Likely To, Secret, Two Truths & A Lie, Would I Lie To You
private val revealBlue = Color(0xFF3A6EA5)   // Truth, Statements, Opinion, Random
private val fallbackColor = Color(0xFF4A4A55)

val categoryColors: Map<String, Color> = mapOf(
    "Dare" to spicyRed,
    "Personal Actions" to spicyRed,
    "The Dictator" to spicyRed,

    "Most Likely To" to socialGreen,
    "Secret" to socialGreen,
    "Two Truths & A Lie" to socialGreen,
    "Would I Lie To You" to socialGreen,

    "Truth" to revealBlue,
    "Statements" to revealBlue,
    "Opinion" to revealBlue,
    "Random" to revealBlue
)

fun colorForCategory(category: String?): Color {
    if (category == null) return fallbackColor
    return categoryColors[category] ?: fallbackColor
}