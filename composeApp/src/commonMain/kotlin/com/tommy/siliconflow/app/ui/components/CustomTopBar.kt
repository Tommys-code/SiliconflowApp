package com.tommy.siliconflow.app.ui.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_back

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String,
    popBack: (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(title)
        }, navigationIcon = {
            popBack?.let {
                IconButton(onClick = it) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_back),
                        contentDescription = "drawer",
                    )
                }
            }
        }
    )
}