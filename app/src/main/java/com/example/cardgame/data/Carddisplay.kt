package com.example.cardgame.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Detail Shapes

@Serializable
private data class TextDetail(val text: String)

@Serializable
private data class TwoTruthsDetail(val truth1: String, val truth2: String, val lie: String)

@Serializable
private data class WouldILieDetail(val wingman: String, val isTrue: String, val story: String)

// Display Model
// What the game screen actually renders: a list of text lines for a card.

data class CardDisplay(
    val category: String,
    val lines: List<String>
)

// Parser

private val detailParser = Json { ignoreUnknownKeys = true }

private const val targetToken = "@TargetName"

fun Card.toDisplay(): CardDisplay {
    return when (category) {
        "Two Truths & A Lie" -> {
            val d = detailParser.decodeFromString<TwoTruthsDetail>(details)
            val shuffled = listOf(d.truth1, d.truth2, d.lie)
                .map { it.replaceTarget(target) }
                .shuffled()
            CardDisplay(category, shuffled)
        }

        "Would I Lie To You" -> {
            val d = detailParser.decodeFromString<WouldILieDetail>(details)
            CardDisplay(
                category,
                listOf(
                    d.story.replaceTarget(target),
                    "Wingman: ${d.wingman}"
                )
            )
        }

        else -> {
            val d = detailParser.decodeFromString<TextDetail>(details)
            CardDisplay(category, listOf(d.text.replaceTarget(target)))
        }
    }
}

private fun String.replaceTarget(target: String): String {
    return this.replace(targetToken, target)
}