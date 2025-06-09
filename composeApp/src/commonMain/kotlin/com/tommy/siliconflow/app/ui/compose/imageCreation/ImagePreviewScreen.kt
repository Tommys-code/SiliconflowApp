package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.LocalPlatformContext
import com.tommy.siliconflow.app.ui.components.CustomTopBar
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.ui.components.PagerIndicator
import com.tommy.siliconflow.app.ui.components.Toast
import com.tommy.siliconflow.app.ui.components.saveImage
import com.tommy.siliconflow.app.ui.theme.AppColor
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_download
import siliconflowapp.composeapp.generated.resources.save_failed
import siliconflowapp.composeapp.generated.resources.save_success

@Composable
fun ImagePreviewScreen(
    urls: List<String>,
    current: Int,
    popBack: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { urls.size })
    val scope = rememberCoroutineScope()
    val context = LocalPlatformContext.current
    val hostState = SnackbarHostState()

    fun saveToLocal() {
        scope.launch {
            val result = saveImage(context, urls[pagerState.currentPage])
            val res = if (result) {
                Res.string.save_success
            } else {
                Res.string.save_failed
            }
            hostState.showSnackbar(getString(res))
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = "",
                containerColor = AppColor.Transparent,
                popBack = popBack,
                actions = {
                    IconButton(onClick = { saveToLocal() }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_download),
                            contentDescription = "download",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            HorizontalPager(pagerState) {
                ImageItem(
                    urls[it],
                    modifier = Modifier.fillMaxSize().zoomable(rememberZoomState())
                )
            }
            PagerIndicator(
                pagerState.pageCount,
                pagerState.currentPage,
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .padding(innerPadding)
            )
            Toast(hostState)
        }
    }

    LaunchedEffect(Unit) {
        pagerState.scrollToPage(current)
    }
}
