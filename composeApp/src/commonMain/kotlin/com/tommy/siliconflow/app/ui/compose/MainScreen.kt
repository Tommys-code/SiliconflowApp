package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.data.db.Session
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.ui.components.Toast
import com.tommy.siliconflow.app.ui.dialog.MainViewDialog
import com.tommy.siliconflow.app.ui.dialog.SessionPopup
import com.tommy.siliconflow.app.ui.theme.CommonColor
import com.tommy.siliconflow.app.viewmodel.MainViewEvent
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.create_new_session
import siliconflowapp.composeapp.generated.resources.ic_circle_add
import siliconflowapp.composeapp.generated.resources.ic_dehaze
import siliconflowapp.composeapp.generated.resources.ic_settings
import siliconflowapp.composeapp.generated.resources.title

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
