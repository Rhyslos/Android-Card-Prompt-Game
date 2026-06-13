package com.example.cardgame.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Feed Model

@Serializable
data class FeedCard(
    val target: String,
    val category: String,
    val details: String,
    val timestamp: String,
    val id: Int
)

// Parsing

private val jsonParser = Json { ignoreUnknownKeys = true }

fun parseFeed(rawJson: String): List<FeedCard> {
    return jsonParser.decodeFromString(rawJson)
}

fun FeedCard.toCard(idOffset: Int = 0): Card {
    return Card(
        id = id + idOffset,
        target = target,
        category = category,
        details = details,
        timestamp = timestamp
    )
}