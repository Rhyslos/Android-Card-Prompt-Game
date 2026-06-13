package com.example.cardgame.data

// Gamemode Abstraction
// Each gamemode knows its identity and how to build its own card pool.
// To add a new gamemode: create a new object implementing GameMode and add it to allGameModes.

interface GameMode {
    val name: String
    val description: String
    suspend fun buildPool(dao: CardDao): List<Card>
}

// Splash
// Per-category caps, unused cards only, shuffled into a mixed deck.

object Splash : GameMode {
    override val name = "Splash"
    override val description = "A curated mix from every category. Limited cards per category, unused only."

    override suspend fun buildPool(dao: CardDao): List<Card> {
        val pool = mutableListOf<Card>()
        val categories = dao.getAllCards().map { it.category }.distinct()

        for (category in categories) {
            val cap = categoryCaps[category] ?: defaultCategoryCap
            val unused = dao.getUnusedCardsByCategory(category)
            pool.addAll(unused.shuffled().take(cap))
        }

        return pool.shuffled()
    }
}

// Shufflemaster
// Every card (used and unused), reshuffled fresh each start, capped at 50 total.

object Shufflemaster : GameMode {
    override val name = "Shufflemaster"
    override val description = "Pure chaos. Every card shuffled fresh, capped at 50 for the round."

    private const val totalCap = 50

    override suspend fun buildPool(dao: CardDao): List<Card> {
        return dao.getAllCards().shuffled().take(totalCap)
    }
}

// Registry
// All gamemodes shown in the grid. Order here is the order they appear (first = top-left).

val allGameModes: List<GameMode> = listOf(
    Splash,
    Shufflemaster
)