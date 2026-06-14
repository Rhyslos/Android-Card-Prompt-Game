package com.example.cardgame.data

// Gamemode Abstraction
// buildPool returns a PoolResult: the deck plus any category shortfalls (for warnings).
//
// Some modes need configuration (category toggles, weights) gathered BEFORE building.
// That is described by configType; the UI shows the matching popup, then calls buildPool
// with the chosen config. Stage 1: config popups not wired yet, so Custom modes behave
// like their base mode.

data class PoolResult(
    val deck: List<Card>,
    val shortfalls: List<String> = emptyList()
)

enum class ConfigType {
    NONE,            // start immediately
    CATEGORY_TOGGLE, // pick which categories are on/off
    FULL_CUSTOM      // deck size + weights + category toggles
}

// GameConfig: the choices a popup feeds back into buildPool.

data class GameConfig(
    val allowedCategories: Set<String>? = null,
    val weights: Map<String, Int>? = null,
    val deckSize: Int = splashDeckSize
)

interface GameMode {
    val name: String
    val description: String
    val configType: ConfigType
    suspend fun buildPool(dao: CardDao, config: GameConfig): PoolResult
}

// Splash

object Splash : GameMode {
    override val name = "Splash"
    override val description = "A curated weighted mix from every category. Unused only."
    override val configType = ConfigType.NONE

    override suspend fun buildPool(dao: CardDao, config: GameConfig): PoolResult {
        val r = buildSplashPool(dao)
        return PoolResult(r.deck, r.shortfalls)
    }
}

// Shufflemaster

object Shufflemaster : GameMode {
    override val name = "Shufflemaster"
    override val description = "Pure chaos. Every card shuffled fresh, capped at 50."
    override val configType = ConfigType.NONE

    override suspend fun buildPool(dao: CardDao, config: GameConfig): PoolResult {
        return PoolResult(buildShufflePool(dao))
    }
}

// Custom Splash (Splash with a category filter)

object CustomSplash : GameMode {
    override val name = "Custom Splash"
    override val description = "Splash, but you choose which categories are in play."
    override val configType = ConfigType.CATEGORY_TOGGLE

    override suspend fun buildPool(dao: CardDao, config: GameConfig): PoolResult {
        val r = buildSplashPool(dao, allowedCategories = config.allowedCategories)
        return PoolResult(r.deck, r.shortfalls)
    }
}

// Custom Shufflemaster (Shufflemaster with a category filter)

object CustomShufflemaster : GameMode {
    override val name = "Custom Shufflemaster"
    override val description = "Shufflemaster, but you choose which categories are in play."
    override val configType = ConfigType.CATEGORY_TOGGLE

    override suspend fun buildPool(dao: CardDao, config: GameConfig): PoolResult {
        return PoolResult(buildShufflePool(dao, allowedCategories = config.allowedCategories))
    }
}

// Single Category Mode (generated per category)

class CategoryMode(private val category: String) : GameMode {
    override val name = category
    override val description = "Play only $category cards, shuffled."
    override val configType = ConfigType.NONE

    override suspend fun buildPool(dao: CardDao, config: GameConfig): PoolResult {
        return PoolResult(buildCategoryPool(dao, category))
    }
}

// Custom Game (full control)

object CustomGame : GameMode {
    override val name = "Custom Game"
    override val description = "Set deck size, per-category weights, and which categories play."
    override val configType = ConfigType.FULL_CUSTOM

    override suspend fun buildPool(dao: CardDao, config: GameConfig): PoolResult {
        val r = buildSplashPool(
            dao,
            allowedCategories = config.allowedCategories,
            weightsOverride = config.weights,
            deckSize = config.deckSize
        )
        return PoolResult(r.deck, r.shortfalls)
    }
}

// Registry Builder
// Assembled at runtime from the categories present in the DB.
// Order: Splash, Shufflemaster, Custom Splash, Custom Shufflemaster,
//        [one mode per category], Custom Game.

fun buildGameModeList(categories: List<String>): List<GameMode> {
    val modes = mutableListOf<GameMode>(
        Splash,
        Shufflemaster,
        CustomSplash,
        CustomShufflemaster
    )
    modes.addAll(categories.sorted().map { CategoryMode(it) })
    modes.add(CustomGame)
    return modes
}