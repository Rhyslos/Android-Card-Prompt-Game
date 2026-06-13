package com.example.cardgame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardgame.data.Card
import com.example.cardgame.data.toDisplay

// Tap Zone Debug Colors
// Set to true to see the tappable areas. Set to false to hide them in normal play.

private const val showTapZones = false
private val nextZoneColor = Color(0x3300FF00)
private val prevZoneColor = Color(0x33FF0000)

// Game Screen

@Composable
fun GameScreen(
    currentCard: Card?,
    isFinished: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isFinished) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Deck finished!")
        }
        return
    }

    val display = currentCard?.toDisplay()

    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = display?.category ?: "",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            display?.lines?.forEach { line ->
                Text(
                    text = line,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // Tap Zones Layout
        Row(modifier = Modifier.fillMaxSize()) {

            // Previous Zone (15% left side)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.15f)
                    .background(if (showTapZones) prevZoneColor else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onPrevious() }
            )

            // Dead Zone (65% middle)
            Spacer(modifier = Modifier.weight(0.65f))

            // Next Zone (20% right side)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.20f)
                    .background(if (showTapZones) nextZoneColor else Color.Transparent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNext() }
            )
        }
    }
}