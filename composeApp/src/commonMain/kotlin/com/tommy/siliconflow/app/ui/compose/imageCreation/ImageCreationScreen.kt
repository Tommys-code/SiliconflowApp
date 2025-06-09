package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.navigation.AppScreen
import com.tommy.siliconflow.app.ui.components.CustomTopBar
import com.tommy.siliconflow.app.ui.components.Toast
import com.tommy.siliconflow.app.viewmodel.ImageCreationEvent
import com.tommy.siliconflow.app.viewmodel.ImageCreationViewModel
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.getString
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
    val focusManager = LocalFocusManager.current
    val viewModel: ImageCreationViewModel = koinViewModel(parameters = { parametersOf(sessionID) })
    val hostState = SnackbarHostState()
    Scaffold(
        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
            detectTapGestures(onPress = {
                keyboardController?.hide()
                focusManager.clearFocus()
            })
        },
        snackbarHost = { Toast(hostState, modifier = Modifier.padding(bottom = 80.dp)) },
        topBar = { CustomTopBar(title = stringResource(Res.string.image_creation_title)) { popBack() } }) { innerPadding ->
        ImageGenerationView(sessionID = sessionID, modifier = Modifier.padding(innerPadding))
    }
    LaunchedEffect(Unit) {
        viewModel.viewEvent.collectLatest {
            when (it) {
                is ImageCreationEvent.Navigate -> navigateTo(it.route)
                is ImageCreationEvent.ShowToast -> hostState.showSnackbar(getString(it.msg))
                else -> {}
            }
        }
    }
}