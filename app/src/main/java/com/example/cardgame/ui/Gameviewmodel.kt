package com.example.cardgame.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardgame.data.Card
import com.example.cardgame.data.CardDatabase
import com.example.cardgame.data.GameMode
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

    fun startGame(mode: GameMode) {
        viewModelScope.launch {
            val pool = mode.buildPool(dao)
            _deck.value = pool
            _currentIndex.value = 0
            _isFinished.value = pool.isEmpty()
            _currentCard.value = pool.firstOrNull()
        }
    }

    fun next() {
        val deck = _deck.value
        if (deck.isEmpty()) return

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