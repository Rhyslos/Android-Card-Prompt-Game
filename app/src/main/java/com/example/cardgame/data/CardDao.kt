package com.example.cardgame.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Card DAO

@Dao
interface CardDao {

    // Reads

    @Query("SELECT * FROM cards")
    suspend fun getAllCards(): List<Card>

    @Query("SELECT * FROM cards WHERE category = :category AND isUsed = 0")
    suspend fun getUnusedCardsByCategory(category: String): List<Card>

    @Query("SELECT id FROM cards")
    suspend fun getAllIds(): List<Int>

    @Query("SELECT COUNT(*) FROM cards")
    suspend fun getCardCount(): Int

    // Writes

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNewCards(cards: List<Card>)

    @Query("UPDATE cards SET useCount = useCount + 1, isUsed = 1 WHERE id = :cardId")
    suspend fun markCardUsed(cardId: Int)

    @Query("UPDATE cards SET useCount = 0, isUsed = 0")
    suspend fun resetAllTracking()

    @Query("DELETE FROM cards")
    suspend fun deleteAllCards()
}