package com.example.cardgame.data

// Per-Category Caps
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

// Pool Builder

suspend fun buildMainPool(dao: CardDao): List<Card> {
    val pool = mutableListOf<Card>()

    val categories = dao.getAllCards().map { it.category }.distinct()

    for (category in categories) {
        val cap = categoryCaps[category] ?: defaultCategoryCap
        val unused = dao.getUnusedCardsByCategory(category)
        val picked = unused.shuffled().take(cap)
        pool.addAll(picked)
    }

    return pool.shuffled()
}