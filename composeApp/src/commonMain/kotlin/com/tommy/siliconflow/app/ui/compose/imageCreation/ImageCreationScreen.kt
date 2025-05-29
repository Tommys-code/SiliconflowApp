package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.tommy.siliconflow.app.ui.components.CustomTopBar

@Composable
fun ImageCreationScreen(
    sessionID: Long?,
    popBack: () -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(modifier = Modifier.fillMaxSize().pointerInput(Unit) {
        detectTapGestures { _ ->
            keyboardController?.hide()
        }
    }, topBar = { CustomTopBar(title = "") { popBack() } }) { innerPadding ->
        ImageGenerationView(sessionID = sessionID, modifier = Modifier.padding(innerPadding))
    }
}