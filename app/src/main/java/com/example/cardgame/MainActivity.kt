package com.example.cardgame

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cardgame.data.GameConfig
import com.example.cardgame.data.GameMode
import com.example.cardgame.data.GamePrefs
import com.example.cardgame.data.PlayerPrefs
import com.example.cardgame.data.buildGameModeList
import com.example.cardgame.data.categoryWeights
import com.example.cardgame.data.ConfigType
import com.example.cardgame.ui.GameViewModel
import com.example.cardgame.ui.SyncStatus
import com.example.cardgame.ui.SyncViewModel
import com.example.cardgame.ui.screens.DebugCardListDialog
import com.example.cardgame.ui.screens.DebugMenuDialog
import com.example.cardgame.ui.screens.GameModeScreen
import com.example.cardgame.ui.screens.GameScreen
import com.example.cardgame.ui.screens.PlayerSetupScreen
import com.example.cardgame.ui.screens.SyncFailedDialog
import com.example.cardgame.ui.screens.ShortfallDialog
import com.example.cardgame.ui.screens.CategoryToggleDialog
import com.example.cardgame.ui.screens.CustomGameDialog
import com.example.cardgame.ui.screens.SyncIndicator
import com.example.cardgame.ui.screens.WipeConfirmDialog
import com.example.cardgame.ui.screens.WipeDbConfirmDialog
import com.example.cardgame.ui.theme.CardGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            CardGameTheme {

                val context = LocalContext.current
                val playerPrefs = remember { PlayerPrefs(context) }
                val gamePrefs = remember { GamePrefs(context) }

                val syncViewModel: SyncViewModel = viewModel()
                val gameViewModel: GameViewModel = viewModel()

                val syncStatus by syncViewModel.syncStatus.collectAsState()
                val hasExistingCards by syncViewModel.hasExistingCards.collectAsState()
                val debugCards by syncViewModel.debugCards.collectAsState()

                val currentCard by gameViewModel.currentCard.collectAsState()
                val isFinished by gameViewModel.isFinished.collectAsState()
                val pendingShortfalls by gameViewModel.pendingShortfalls.collectAsState()
                val gameStarted by gameViewModel.gameStarted.collectAsState()
                val categories by gameViewModel.categories.collectAsState()
                val gameModes = remember(categories) { buildGameModeList(categories) }

                var currentScreen by rememberSaveable { mutableStateOf("setup") }
                var dismissedFailure by rememberSaveable { mutableStateOf(false) }

                var showDebugMenu by rememberSaveable { mutableStateOf(false) }
                var showCardList by rememberSaveable { mutableStateOf(false) }
                var showWipeConfirm by rememberSaveable { mutableStateOf(false) }
                var showWipeDbConfirm by rememberSaveable { mutableStateOf(false) }
                var showTapZones by rememberSaveable { mutableStateOf(false) }

                // Config popup state: which mode is awaiting configuration
                var configMode by remember { mutableStateOf<GameMode?>(null) }

                LaunchedOrientation(currentScreen)

                LaunchedEffect(gameStarted) {
                    if (gameStarted) {
                        currentScreen = "game"
                        gameViewModel.clearGameStarted()
                    }
                }

                BackHandler(enabled = currentScreen != "setup") {
                    when (currentScreen) {
                        "gamemode" -> currentScreen = "setup"
                        "game" -> currentScreen = "gamemode"
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {

                        when (currentScreen) {
                            "setup" -> PlayerSetupScreen(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .padding(top = 64.dp),
                                onContinue = { currentScreen = "gamemode" }
                            )

                            "gamemode" -> {
                                LaunchedEffect(Unit) { gameViewModel.loadCategories() }
                                GameModeScreen(
                                    gameModes = gameModes,
                                    modifier = Modifier
                                        .padding(innerPadding)
                                        .padding(top = 64.dp),
                                    onGameModeSelected = { mode ->
                                        when (mode.configType) {
                                            ConfigType.NONE -> gameViewModel.prepareGame(mode)
                                            else -> configMode = mode
                                        }
                                    }
                                )
                            }

                            "game" -> GameScreen(
                                currentCard = currentCard,
                                isFinished = isFinished,
                                showTapZones = showTapZones,
                                onNext = { gameViewModel.next() },
                                onPrevious = { gameViewModel.previous() },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        if (currentScreen == "setup") {
                            IconButton(
                                onClick = { showDebugMenu = true },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(innerPadding)
                                    .padding(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Build, contentDescription = "Debug")
                            }
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

                        val activeConfigMode = configMode
                        if (activeConfigMode != null) {
                            when (activeConfigMode.configType) {
                                ConfigType.CATEGORY_TOGGLE -> {
                                    val saved = gamePrefs.getEnabledCategories(activeConfigMode.name)
                                    val initial = saved ?: categories.toSet()
                                    CategoryToggleDialog(
                                        categories = categories,
                                        initiallyEnabled = initial,
                                        onStart = { enabled ->
                                            gamePrefs.saveEnabledCategories(activeConfigMode.name, enabled)
                                            gameViewModel.prepareGame(
                                                activeConfigMode,
                                                GameConfig(allowedCategories = enabled)
                                            )
                                            configMode = null
                                        },
                                        onDismiss = { configMode = null }
                                    )
                                }

                                ConfigType.FULL_CUSTOM -> {
                                    val savedWeights = gamePrefs.getWeights() ?: categoryWeights
                                    val savedSize = gamePrefs.getDeckSize()
                                    CustomGameDialog(
                                        categories = categories,
                                        initialWeights = savedWeights,
                                        initialDeckSize = savedSize,
                                        onStart = { deckSize, weights ->
                                            gamePrefs.saveWeights(weights)
                                            gamePrefs.saveDeckSize(deckSize)
                                            val enabled = weights.filterValues { it > 0 }.keys.toSet()
                                            gameViewModel.prepareGame(
                                                activeConfigMode,
                                                GameConfig(
                                                    allowedCategories = enabled,
                                                    weights = weights,
                                                    deckSize = deckSize
                                                )
                                            )
                                            configMode = null
                                        },
                                        onDismiss = { configMode = null }
                                    )
                                }

                                ConfigType.NONE -> {}
                            }
                        }

                        if (pendingShortfalls.isNotEmpty()) {
                            ShortfallDialog(
                                shortfalls = pendingShortfalls,
                                onPlayAnyway = { gameViewModel.confirmPendingGame() },
                                onGoBack = { gameViewModel.cancelPendingGame() }
                            )
                        }

                        if (showDebugMenu) {
                            DebugMenuDialog(
                                showTapZones = showTapZones,
                                onToggleTapZones = { showTapZones = it },
                                onViewCards = {
                                    syncViewModel.loadDebugCards()
                                    showDebugMenu = false
                                    showCardList = true
                                },
                                onWipeCounter = {
                                    showDebugMenu = false
                                    showWipeConfirm = true
                                },
                                onWipeDb = {
                                    showDebugMenu = false
                                    showWipeDbConfirm = true
                                },
                                onClearPlayerCache = {
                                    playerPrefs.clearNames()
                                    showDebugMenu = false
                                    Toast.makeText(context, "Cache wiped! Restart app to apply.", Toast.LENGTH_SHORT).show()
                                },
                                onDismiss = { showDebugMenu = false }
                            )
                        }

                        if (showCardList) {
                            DebugCardListDialog(
                                cards = debugCards,
                                onDismiss = { showCardList = false }
                            )
                        }

                        if (showWipeConfirm) {
                            WipeConfirmDialog(
                                onConfirm = {
                                    syncViewModel.wipeCardCounter {
                                        Toast.makeText(context, "Card counter wiped", Toast.LENGTH_SHORT).show()
                                    }
                                    showWipeConfirm = false
                                },
                                onDismiss = { showWipeConfirm = false }
                            )
                        }

                        if (showWipeDbConfirm) {
                            WipeDbConfirmDialog(
                                onConfirm = {
                                    syncViewModel.wipeDatabase {
                                        Toast.makeText(context, "Database wiped", Toast.LENGTH_SHORT).show()
                                    }
                                    showWipeDbConfirm = false
                                },
                                onDismiss = { showWipeDbConfirm = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LaunchedOrientation(currentScreen: String) {
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(currentScreen) {
        activity?.requestedOrientation = if (currentScreen == "game") {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }
}