package com.example.cardgame.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardgame.data.Card
import com.example.cardgame.data.CardDatabase
import com.example.cardgame.data.GameConfig
import com.example.cardgame.data.GameMode
import com.example.cardgame.data.PoolResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Game Session

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = CardDatabase.getInstance(application).cardDao()

    private val _deck = MutableStateFlow<List<Card>>(emptyList())

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _currentCard = MutableStateFlow<Card?>(null)
    val currentCard: StateFlow<Card?> = _currentCard.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished: StateFlow<Boolean> = _isFinished.asStateFlow()

    // Signals the game has actually begun (deck loaded), for navigation.
    private val _gameStarted = MutableStateFlow(false)
    val gameStarted: StateFlow<Boolean> = _gameStarted.asStateFlow()

    // Pending pool awaiting confirmation (when there are shortfalls)
    private val _pendingShortfalls = MutableStateFlow<List<String>>(emptyList())
    val pendingShortfalls: StateFlow<List<String>> = _pendingShortfalls.asStateFlow()

    private var pendingDeck: List<Card> = emptyList()

    // Categories present in the DB, for building the dynamic gamemode grid.
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            _categories.value = dao.getAllCards().map { it.category }.distinct()
        }
    }

    // Build the pool. If shortfalls exist, hold the deck and expose them for a warning.
    // Otherwise start immediately. Stage 1: config is default (no popups yet).
    fun prepareGame(mode: GameMode, config: GameConfig = GameConfig()) {
        viewModelScope.launch {
            val result: PoolResult = mode.buildPool(dao, config)
            if (result.shortfalls.isNotEmpty()) {
                pendingDeck = result.deck
                _pendingShortfalls.value = result.shortfalls
            } else {
                beginWith(result.deck)
            }
        }
    }

    // Called when the user confirms "play anyway" after a shortfall warning.
    fun confirmPendingGame() {
        beginWith(pendingDeck)
        _pendingShortfalls.value = emptyList()
        pendingDeck = emptyList()
    }

    // Called when the user backs out of the shortfall warning.
    fun cancelPendingGame() {
        _pendingShortfalls.value = emptyList()
        pendingDeck = emptyList()
    }

    private fun beginWith(pool: List<Card>) {
        _deck.value = pool
        _currentIndex.value = 0
        _isFinished.value = pool.isEmpty()
        _currentCard.value = pool.firstOrNull()
        _gameStarted.value = true
    }

    // Reset the started flag when leaving the game screen.
    fun clearGameStarted() {
        _gameStarted.value = false
    }

    fun next() {
        val deck = _deck.value
        if (deck.isEmpty() || _isFinished.value) return

        val leaving = deck.getOrNull(_currentIndex.value)
        if (leaving != null) {
            viewModelScope.launch { dao.markCardUsed(leaving.id) }
        }

        val nextIndex = _currentIndex.value + 1
        if (nextIndex >= deck.size) {
            _isFinished.value = true
            _currentCard.value = null
        } else {
            _currentIndex.value = nextIndex
            _currentCard.value = deck[nextIndex]
        }
    }

    fun previous() {
        val deck = _deck.value
        if (deck.isEmpty()) return

        val prevIndex = _currentIndex.value - 1
        if (prevIndex >= 0) {
            _currentIndex.value = prevIndex
            _currentCard.value = deck[prevIndex]
        }
    }
}