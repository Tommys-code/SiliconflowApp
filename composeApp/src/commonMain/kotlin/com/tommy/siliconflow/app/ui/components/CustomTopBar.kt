package com.tommy.siliconflow.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.painterResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_back
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun CustomTopBar(
    title: String,
    containerColor: Color? = null,
    actions: @Composable RowScope.() -> Unit = {},
    popBack: (() -> Unit)? = null,
) {
    val colors = containerColor?.let {
        TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor)
    } ?: TopAppBarDefaults.centerAlignedTopAppBarColors()

    CenterAlignedTopAppBar(
        title = {
            Text(title)
        },
        colors = colors,
        navigationIcon = {
            var lastClickTime = 0L
            popBack?.let {
                IconButton(onClick = {
                    val currentTime = Clock.System.now().toEpochMilliseconds()
                    if (currentTime - lastClickTime > 1000L) {
                        lastClickTime = currentTime
                        popBack()
                    }
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "drawer",
                    )
                }
            }
        },
        actions = actions,
    )
}

@OptIn(ExperimentalTime::class)
fun Modifier.debounceClick(
    debounceTime: Long = 300L,
    onClick: () -> Unit
): Modifier {
    var lastClickTime = 0L
    return clickable {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        if (currentTime - lastClickTime > debounceTime) {
            lastClickTime = currentTime
            onClick()
        }
    }
}