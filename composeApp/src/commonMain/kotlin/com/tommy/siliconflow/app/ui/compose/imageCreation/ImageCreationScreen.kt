package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.tommy.siliconflow.app.navigation.AppScreen
import com.tommy.siliconflow.app.ui.components.CustomTopBar
import com.tommy.siliconflow.app.viewmodel.ImageCreationEvent
import com.tommy.siliconflow.app.viewmodel.ImageCreationViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.image_creation_title

@Composable
fun ImageCreationScreen(
    sessionID: Long?,
    popBack: () -> Unit,
    navigateTo: (AppScreen) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val viewModel: ImageCreationViewModel = koinViewModel(parameters = { parametersOf(sessionID) })
    Scaffold(
        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures { _ ->
                keyboardController?.hide()
            }
        },
        topBar = { CustomTopBar(title = stringResource(Res.string.image_creation_title)) { popBack() } }) { innerPadding ->
        ImageGenerationView(sessionID = sessionID, modifier = Modifier.padding(innerPadding))
    }
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collectLatest {
            if (it is ImageCreationEvent.Navigate) {
                navigateTo(it.route)
            }
        }
    }
}