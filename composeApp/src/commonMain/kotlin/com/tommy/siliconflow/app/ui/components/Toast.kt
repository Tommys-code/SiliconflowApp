package com.tommy.siliconflow.app.ui.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Toast(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(hostState, modifier) {
        Card(
            Modifier
                .defaultMinSize(minWidth = 96.dp, minHeight = 48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Text(
                text = it.visuals.message,
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(vertical = 20.dp)
            )
        }
    }
}

