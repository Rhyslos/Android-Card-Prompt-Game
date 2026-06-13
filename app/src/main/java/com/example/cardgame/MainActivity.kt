package com.example.cardgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.cardgame.ui.screens.GameModeScreen
import com.example.cardgame.ui.screens.PlayerSetupScreen
import com.example.cardgame.ui.theme.CardGameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CardGameTheme {
                var currentScreen by remember { mutableStateOf("setup") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                }
            }
        }
    }
}