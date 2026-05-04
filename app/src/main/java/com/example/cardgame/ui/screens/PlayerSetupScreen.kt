package com.example.cardgame.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PrintHelloWorld(modifier: Modifier) {
    Column {
        Text(
            text = "Hello, New World from new!",
            modifier = Modifier
        )
    }
}

fun AddNamesToList() {

}

@Composable
fun AddPlayerButton(modifier: Modifier = Modifier, onClick: () -> Unit){
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ElevatedButton(onClick = onClick, modifier = Modifier,
            ) {
            Text( text = "Add Player")
        }
        ElevatedButton(onClick = onClick, modifier = Modifier ) {
            Text( text = "Add Player")
        }
        ElevatedButton(onClick = onClick, modifier = Modifier ) {
            Text( text = "Add Player")
        }
    }
}