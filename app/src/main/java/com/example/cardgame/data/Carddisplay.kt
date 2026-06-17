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

data class CardDisplay(
    val category: String,
    val target: String,
    val lines: List<String>,
    val isError: Boolean = false
)

// Parser

private val detailParser = Json { ignoreUnknownKeys = true }

private const val targetToken = "@TargetName"

fun Card.toDisplay(): CardDisplay {
    return try {
        when (category) {
            "Two Truths & A Lie" -> {
                val d = detailParser.decodeFromString<TwoTruthsDetail>(details)
                val shuffled = listOf(d.truth1, d.truth2, d.lie)
                    .map { it.replaceTarget(target) }
                    .shuffled()
                CardDisplay(category, target, shuffled)
            }

            "Would I Lie To You" -> {
                val d = detailParser.decodeFromString<WouldILieDetail>(details)
                CardDisplay(
                    category,
                    target,
                    listOf(
                        d.story.replaceTarget(target),
                        "Wingman: ${d.wingman}"
                    )
                )
            }

            else -> {
                val d = detailParser.decodeFromString<TextDetail>(details)
                CardDisplay(category, target, listOf(d.text.replaceTarget(target)))
            }
        }
    } catch (e: Exception) {
        CardDisplay(
            category = category,
            target = target,
            lines = listOf(
                "Card couldn't be read.",
                "Bad card ID: $id",
                "Tap to skip."
            ),
            isError = true
        )
    }
}

private fun String.replaceTarget(target: String): String {
    return this.replace(targetToken, target)
}