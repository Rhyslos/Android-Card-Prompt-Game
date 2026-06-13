package com.example.cardgame.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.cardgame.ui.SyncStatus
import kotlinx.coroutines.delay

@Composable
fun SyncIndicator(status: SyncStatus, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(status) {
        if (status == SyncStatus.Success) {
            delay(3000)
            visible = false
        } else {
            visible = true
        }
    }

    if (!visible) return

    when (status) {
        SyncStatus.Syncing -> {
            val transition = rememberInfiniteTransition(label = "syncSpin")
            val angle by transition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "syncAngle"
            )
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Syncing",
                modifier = modifier
                    .size(24.dp)
                    .rotate(angle)
            )
        }

        SyncStatus.Success -> {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sync complete",
                modifier = modifier.size(24.dp)
            )
        }

        SyncStatus.Failed -> {}
    }
}