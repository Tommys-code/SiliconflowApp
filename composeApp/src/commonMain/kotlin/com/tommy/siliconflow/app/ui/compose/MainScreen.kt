package com.tommy.siliconflow.app.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.Greeting
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.viewmodel.MainViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.compose_multiplatform
import siliconflowapp.composeapp.generated.resources.ic_dehaze
import siliconflowapp.composeapp.generated.resources.ic_settings
import siliconflowapp.composeapp.generated.resources.title

@Composable
internal fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    var showContent by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerContent = { DrawerContent(viewModel) },
        drawerState = viewModel.drawerState,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(), topBar = {
                HomeTopAppBar { viewModel.toggleDrawer(coroutineScope) }
            }) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = { showContent = !showContent }) {
                    Text("Click me!")
                }
                AnimatedVisibility(showContent) {
                    val greeting = remember { Greeting().greet() }
                    Column(
                        Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painterResource(Res.drawable.compose_multiplatform), null)
                        Text("Compose: $greeting")
                    }
                }
            }
        }
    }
}

@Composable
internal fun DrawerContent(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val userInfo = viewModel.userInfo.collectAsState(null).value?.result
    Column(
        modifier = modifier.fillMaxHeight().fillMaxWidth(0.7f).background(Color.White)
            .safeContentPadding(),
    ) {
        Text(
            stringResource(Res.string.title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp)
        )
        HorizontalDivider()
        LazyColumn(modifier = Modifier.weight(1f)) { }
        HorizontalDivider()
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ImageItem(
                userInfo?.image,
                modifier = Modifier.padding(end = 8.dp).size(36.dp).clip(CircleShape)
            )
            Text(userInfo?.name.orEmpty(), modifier.weight(1f))
            Icon(
                painter = painterResource(Res.drawable.ic_settings),
                contentDescription = "setting",
                modifier = Modifier.clickable { })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeTopAppBar(navigationClick: () -> Unit) {
    CenterAlignedTopAppBar(title = {}, navigationIcon = {
        IconButton(onClick = navigationClick) {
            Icon(
                painter = painterResource(Res.drawable.ic_dehaze),
                contentDescription = "drawer",
            )
        }
    })
}