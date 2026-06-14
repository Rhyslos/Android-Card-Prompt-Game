package com.example.cardgame.data

// Splash Configuration
// Weights are RELATIVE importance, not exact counts. The builder distributes
// splashDeckSize slots proportionally by weight, jitters each by +/- splashJitter,
// caps at available unused cards, then redistributes any shortfall to hit the target.

const val splashDeckSize = 50
const val splashJitter = 2
const val splashWarnOnShortfall = true

val categoryWeights: Map<String, Int> = mapOf(
    "Personal Actions" to 10,
    "Most Likely To" to 8,
    "Random" to 7,
    "Dare" to 6,
    "Opinion" to 5,
    "Truth" to 4,
    "The Dictator" to 3,
    "Statements" to 3,
    "Secret" to 2,
    "Two Truths & A Lie" to 1,
    "Would I Lie To You" to 1
)

const val defaultCategoryWeight = 3

// Splash Build Result

data class SplashResult(
    val deck: List<Card>,
    val shortfalls: List<String>
)

// Splash Pool Builder
// allowedCategories: if provided, only these categories are used.
// weightsOverride: if provided, replaces categoryWeights (for Custom Game).
// deckSize: total target (defaults to splashDeckSize).

suspend fun buildSplashPool(
    dao: CardDao,
    allowedCategories: Set<String>? = null,
    weightsOverride: Map<String, Int>? = null,
    deckSize: Int = splashDeckSize
): SplashResult {
    val weights = weightsOverride ?: categoryWeights

    val allCards = dao.getAllCards()
    var categories = allCards.map { it.category }.distinct()
    if (allowedCategories != null) {
        categories = categories.filter { it in allowedCategories }
    }

    if (categories.isEmpty()) return SplashResult(emptyList(), emptyList())

    val unusedByCategory = mutableMapOf<String, MutableList<Card>>()
    for (category in categories) {
        unusedByCategory[category] = dao.getUnusedCardsByCategory(category).shuffled().toMutableList()
    }

    val totalWeight = categories.sumOf { weights[it] ?: defaultCategoryWeight }
    val targets = mutableMapOf<String, Int>()
    for (category in categories) {
        val weight = weights[category] ?: defaultCategoryWeight
        val proportional = (deckSize.toDouble() * weight / totalWeight).toInt()
        val jitter = (-splashJitter..splashJitter).random()
        targets[category] = (proportional + jitter).coerceAtLeast(0)
    }

    val deck = mutableListOf<Card>()
    val shortfalls = mutableListOf<String>()
    for (category in categories) {
        val available = unusedByCategory[category] ?: mutableListOf()
        val want = targets[category] ?: 0
        val take = minOf(want, available.size)
        if (want > available.size) shortfalls.add(category)
        repeat(take) { deck.add(available.removeAt(0)) }
    }

    if (deck.size < deckSize) {
        val leftovers = unusedByCategory.values.flatten().shuffled()
        val needed = deckSize - deck.size
        deck.addAll(leftovers.take(needed))
    }

    val finalDeck = if (deck.size > deckSize) deck.shuffled().take(deckSize) else deck.shuffled()

    return SplashResult(finalDeck, shortfalls)
}

// Shufflemaster Pool Builder
// allowedCategories: if provided, only these categories are used.

suspend fun buildShufflePool(
    dao: CardDao,
    allowedCategories: Set<String>? = null,
    totalCap: Int = 50
): List<Card> {
    var cards = dao.getAllCards()
    if (allowedCategories != null) {
        cards = cards.filter { it.category in allowedCategories }
    }
    return cards.shuffled().take(totalCap)
}

// Single Category Pool Builder
// Simple shuffle of one category (used + unused).

suspend fun buildCategoryPool(dao: CardDao, category: String): List<Card> {
    return dao.getAllCards().filter { it.category == category }.shuffled()
}