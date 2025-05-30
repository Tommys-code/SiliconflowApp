package com.tommy.siliconflow.app.ui.compose.imageCreation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tommy.siliconflow.app.ui.components.CustomTopBar
import com.tommy.siliconflow.app.ui.components.ImageItem
import com.tommy.siliconflow.app.ui.components.PagerIndicator
import com.tommy.siliconflow.app.ui.theme.AppColor
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ImagePreviewScreen(
    urls: List<String>,
    current: Int,
    popBack: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { urls.size })

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar("", containerColor = AppColor.Transparent, popBack = popBack)
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
        }
    }

    LaunchedEffect(Unit) {
        pagerState.scrollToPage(current)
    }
}