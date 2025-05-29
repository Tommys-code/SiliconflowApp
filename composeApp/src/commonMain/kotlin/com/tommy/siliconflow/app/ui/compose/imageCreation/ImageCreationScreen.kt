package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ImageCreationScreen(sessionID: Long?) {
    Scaffold(modifier = Modifier.fillMaxSize()) {
        ImageGenerationView(sessionID = sessionID)
    }
}