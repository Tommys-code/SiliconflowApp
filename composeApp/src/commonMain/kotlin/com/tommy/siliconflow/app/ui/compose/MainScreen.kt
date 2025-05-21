package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tommy.siliconflow.app.ui.components.Toast
import com.tommy.siliconflow.app.ui.dialog.MainViewDialog
import com.tommy.siliconflow.app.ui.dialog.SessionPopup
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_dehaze

@Composable
internal fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    onNavigate: (route: String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val hostState = SnackbarHostState()
    Box {
        ModalNavigationDrawer(
            drawerContent = { DrawerContent(viewModel) { viewModel.doEvent(it) } },
            drawerState = viewModel.mainViewState.drawerState,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    HomeTopAppBar { viewModel.doEvent(it) }
                },
            ) { innerPadding ->
                ChatView(modifier = Modifier.padding(innerPadding), viewModel)
            }
        }
        // popup
        SessionPopup(viewModel.mainViewState.popupState) { viewModel.doEvent(it) }
        MainViewDialog(viewModel.mainViewState.dialogState) { viewModel.doEvent(it) }
        Toast(hostState, modifier = Modifier.align(Alignment.Center))
    }
    coroutineScope.launch {
        viewModel.viewEvent.collect {
            when (it) {
                is MainViewEvent.Navigate -> onNavigate(it.route)
                is MainViewEvent.ShowToast -> it.msg ?: it.msgRes?.let { res ->
                    getString(res)
                }?.let { msg ->
                    hostState.showSnackbar(msg)
                }

                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopAppBar(doEvent: (MainViewEvent) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    CenterAlignedTopAppBar(title = {}, navigationIcon = {
        IconButton(onClick = { doEvent(MainViewEvent.ToggleDrawer(coroutineScope)) }) {
            Icon(
                painter = painterResource(Res.drawable.ic_dehaze),
                contentDescription = "drawer",
            )
        }
    })
}
