package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.data.Resource
import com.tommy.siliconflow.app.navigation.Route
import com.tommy.siliconflow.app.network.error.ApiKeyEmptyException
import com.tommy.siliconflow.app.network.error.GeneralException
import com.tommy.siliconflow.app.ui.components.LoadingDialog
import com.tommy.siliconflow.app.viewmodel.LoginViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.add_key_hilt
import siliconflowapp.composeapp.generated.resources.add_key_title
import siliconflowapp.composeapp.generated.resources.confirm
import siliconflowapp.composeapp.generated.resources.ic_silicon_flow

@Composable
internal fun LoginScreen(
    navigate: (String) -> Unit,
    loginViewModel: LoginViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    val keyValue = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val showDialog = mutableStateOf(false)

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Box(
            modifier = Modifier.fillMaxSize().systemBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_silicon_flow),
                    contentDescription = "logo"
                )
                Text(
                    stringResource(Res.string.add_key_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    value = keyValue.value,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                    label = { Text(stringResource(Res.string.add_key_hilt)) },
                    onValueChange = {
                        keyValue.value = it
                    }
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    shape = RectangleShape,
                    enabled = keyValue.value.isNotBlank(),
                    onClick = {
                        focusManager.clearFocus()
                        loginViewModel.login(keyValue.value)
                    }
                ) {
                    Text(stringResource(Res.string.confirm))
                }
            }
        }
    }
    LoadingDialog(showDialog)

    loginViewModel.userInfo.collectAsState().value.let {
        when (it) {
            is Resource.Error -> {
                showDialog.value = false
                scope.launch {
                    if (it.exception !is ApiKeyEmptyException) {
                        (it.exception as? GeneralException)?.error?.let { msg ->
                            snackbarHostState.showSnackbar(msg)
                        } ?: run {
                            snackbarHostState.showSnackbar(it.exception.message.orEmpty())
                        }
                    }
                }
            }

            is Resource.Success -> {
                showDialog.value = false
                navigate.invoke(Route.MAIN_SCREEN)
            }

            else -> {
                showDialog.value = true
            }
        }
    }

    LaunchedEffect(Unit) {
        loginViewModel.apiKey.collect { keyValue.value = it.orEmpty() }
    }

}