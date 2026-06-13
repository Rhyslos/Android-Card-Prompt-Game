package com.example.cardgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cardgame.ui.SyncStatus
import com.example.cardgame.ui.SyncViewModel
import com.example.cardgame.ui.screens.GameModeScreen
import com.example.cardgame.ui.screens.PlayerSetupScreen
import com.example.cardgame.ui.screens.SyncFailedDialog
import com.example.cardgame.ui.screens.SyncIndicator
import com.example.cardgame.ui.theme.CardGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CardGameTheme {
                val syncViewModel: SyncViewModel = viewModel()
                val syncStatus by syncViewModel.syncStatus.collectAsState()
                val hasExistingCards by syncViewModel.hasExistingCards.collectAsState()

                var currentScreen by remember { mutableStateOf("setup") }
                var dismissedFailure by remember { mutableStateOf(false) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {

                        when (currentScreen) {
                            "setup" -> PlayerSetupScreen(
                                modifier = Modifier.padding(innerPadding),
                                onContinue = { currentScreen = "gamemode" }
                            )

                            "gamemode" -> GameModeScreen(
                                modifier = Modifier.padding(innerPadding),
                                onGameModeSelected = {}
                            )
                        }

                        SyncIndicator(
                            status = syncStatus,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(innerPadding)
                                .padding(16.dp)
                        )

                        if (syncStatus == SyncStatus.Failed && !dismissedFailure) {
                            SyncFailedDialog(
                                hasExistingCards = hasExistingCards,
                                onRetry = { syncViewModel.runSync() },
                                onPlayWithOldDb = { dismissedFailure = true }
                            )
                        }
                    }
                }
            }
        }
    }
}