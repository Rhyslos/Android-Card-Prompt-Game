package com.example.cardgame.data

// Per-Category Caps (used by Splash)
// Change the numbers here to control how many cards each category contributes to the pool.
// Categories not listed use defaultCategoryCap.

const val defaultCategoryCap = 3

val categoryCaps: Map<String, Int> = mapOf(
    "Statements" to 3,
    "Secret" to 3,
    "Truth" to 3,
    "Dare" to 3,
    "Random" to 3,
    "Opinion" to 3,
    "Personal Actions" to 3,
    "The Dictator" to 3,
    "Most Likely To" to 3,
    "Two Truths & A Lie" to 3,
    "Would I Lie To You" to 3
)