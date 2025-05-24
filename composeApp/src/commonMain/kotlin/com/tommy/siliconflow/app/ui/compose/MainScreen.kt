package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.model.LocalAIModel
import com.tommy.siliconflow.app.navigation.AppScreen
import com.tommy.siliconflow.app.ui.components.Toast
import com.tommy.siliconflow.app.ui.dialog.MainViewDialog
import com.tommy.siliconflow.app.ui.dialog.SessionPopup
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_dehaze
import siliconflowapp.composeapp.generated.resources.ic_drop_down

@Composable
internal fun MainScreen(
    viewModel: MainViewModel = koinViewModel(),
    onNavigate: (route: Any) -> Unit,
) {
    val hostState = SnackbarHostState()
    val model = viewModel.currentModel.collectAsStateWithLifecycle(null).value

    Box {
        ModalNavigationDrawer(
            drawerContent = { DrawerContent(viewModel) { viewModel.doEvent(it) } },
            drawerState = viewModel.mainViewState.drawerState,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    HomeTopAppBar(model) { viewModel.doEvent(it) }
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

    LaunchedEffect(Unit) {
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
private fun HomeTopAppBar(
    model: LocalAIModel?,
    doEvent: (MainViewEvent) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    CenterAlignedTopAppBar(title = {
        model?.let {
            TitleView(it.model, doEvent)
        }
    }, navigationIcon = {
        IconButton(onClick = { doEvent(MainViewEvent.ToggleDrawer(coroutineScope)) }) {
            Icon(
                painter = painterResource(Res.drawable.ic_dehaze),
                contentDescription = "drawer",
            )
        }
    })
}

@Composable
private fun TitleView(title: String, doEvent: (MainViewEvent) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { doEvent.invoke(MainViewEvent.Navigate(AppScreen.ModelList)) }
    ) {
        Text(
            title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 200.dp),
        )
        Icon(
            painter = painterResource(Res.drawable.ic_drop_down),
            contentDescription = "more",
        )
    }
}
