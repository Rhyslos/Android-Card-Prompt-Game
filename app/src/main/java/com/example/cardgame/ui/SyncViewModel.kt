package com.example.cardgame.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardgame.data.CardDatabase
import com.example.cardgame.data.fetchRequest
import com.example.cardgame.data.parseFeed
import com.example.cardgame.data.surpriseIdOffset
import com.example.cardgame.data.surpriseSheetUrl
import com.example.cardgame.data.testSheetUrl
import com.example.cardgame.data.toCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Sync State

enum class SyncStatus {
    Syncing,
    Success,
    Failed
}

// ViewModel

class SyncViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = CardDatabase.getInstance(application).cardDao()

    private val _syncStatus = MutableStateFlow(SyncStatus.Syncing)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _hasExistingCards = MutableStateFlow(false)
    val hasExistingCards: StateFlow<Boolean> = _hasExistingCards.asStateFlow()

    init {
        runSync()
    }

    fun runSync() {
        _syncStatus.value = SyncStatus.Syncing
        viewModelScope.launch {
            _hasExistingCards.value = dao.getCardCount() > 0
            try {
                val mainRaw = fetchRequest(testSheetUrl)
                val mainCards = parseFeed(mainRaw).map { it.toCard() }

                val surpriseRaw = fetchRequest(surpriseSheetUrl)
                val surpriseCards = parseFeed(surpriseRaw).map { it.toCard(surpriseIdOffset) }

                dao.insertNewCards(mainCards + surpriseCards)
                _hasExistingCards.value = dao.getCardCount() > 0
                _syncStatus.value = SyncStatus.Success
            } catch (e: Exception) {
                _syncStatus.value = SyncStatus.Failed
            }
        }
    }
}