package com.example.cardgame.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Card Entity

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey val id: Int,
    val target: String,
    val category: String,
    val details: String,
    val timestamp: String,
    val isUsed: Boolean = false,
    val useCount: Int = 0
)