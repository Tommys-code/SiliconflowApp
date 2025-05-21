package com.tommy.siliconflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tommy.siliconflow.app.ui.theme.CommonColor
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.size(120.dp)
            .clip(
                RoundedCornerShape(
                    12.dp
                )
            )
            .background(CommonColor.LoadingBg),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp)
        )
    }
}

@Composable
fun LoadingDialog(showDialog: MutableState<Boolean>) {
    if (showDialog.value) {
        Dialog(
            onDismissRequest = {},
        ) {
            LoadingView()
        }
    }
}