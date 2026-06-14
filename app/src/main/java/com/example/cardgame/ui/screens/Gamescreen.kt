package com.example.cardgame.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardgame.data.Card
import com.example.cardgame.data.toDisplay
import com.example.cardgame.ui.theme.colorForCategory
import kotlinx.coroutines.launch

// Layout Anchors
private val categoryTopPadding = 40.dp
private val textTopAnchor = 120.dp

// Tap Zone Colors
private val nextZoneColor = Color(0x33888888)
private val prevZoneColor = Color(0x33888888)

// Flash feedback timing
private const val flashFadeMillis = 450
private const val flashPeakAlpha = 0.5f

// Custom Shapes for the Tap Zones

class LeftHalfOvalShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            val verticalStretch = size.height * 0.20f
            addOval(
                Rect(
                    left = -size.width * 1.2f,
                    top = -verticalStretch,
                    right = size.width,
                    bottom = size.height + verticalStretch
                )
            )
        }
        return Outline.Generic(path)
    }
}

class RightHalfOvalShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            val verticalStretch = size.height * 0.20f
            addOval(
                Rect(
                    left = 0f,
                    top = -verticalStretch,
                    right = size.width * 2.2f,
                    bottom = size.height + verticalStretch
                )
            )
        }
        return Outline.Generic(path)
    }
}

// Game Screen

@Composable
fun GameScreen(
    currentCard: Card?,
    isFinished: Boolean,
    showTapZones: Boolean,
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

    val display = remember(currentCard) { currentCard?.toDisplay() }

    // Flash alpha for each zone (0 = invisible, fades from full to 0 on tap)
    val leftFlash = remember { Animatable(0f) }
    val rightFlash = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    // Animated background color per category
    val targetColor = colorForCategory(display?.category)
    val backgroundColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 500),
        label = "bgColor"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        // Card content: category fixed near top, text anchored below
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(categoryTopPadding))
            Text(
                text = display?.category ?: "",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(textTopAnchor - categoryTopPadding - 30.dp))
            display?.lines?.forEach { line ->
                Text(
                    text = line,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // Tap Zones
        Row(modifier = Modifier.fillMaxSize()) {

            // Previous Zone
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.15f)
                    .clip(LeftHalfOvalShape())
                    .background(
                        prevZoneColor.copy(
                            alpha = if (showTapZones) prevZoneColor.alpha else prevZoneColor.alpha * leftFlash.value
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onPrevious()
                        scope.launch {
                            leftFlash.snapTo(flashPeakAlpha)
                            leftFlash.animateTo(0f, animationSpec = tween(flashFadeMillis))
                        }
                    }
            )

            // Dead Zone
            Spacer(modifier = Modifier.weight(0.65f))

            // Next Zone
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.20f)
                    .clip(RightHalfOvalShape())
                    .background(
                        nextZoneColor.copy(
                            alpha = if (showTapZones) nextZoneColor.alpha else nextZoneColor.alpha * rightFlash.value
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onNext()
                        scope.launch {
                            rightFlash.snapTo(flashPeakAlpha)
                            rightFlash.animateTo(0f, animationSpec = tween(flashFadeMillis))
                        }
                    }
            )
        }
    }
}