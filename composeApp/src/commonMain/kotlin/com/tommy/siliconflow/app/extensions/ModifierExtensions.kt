package com.tommy.siliconflow.app.extensions

import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

@Composable
fun Modifier.onLongPressClearFocus(longPress: () -> Unit): Modifier {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    return this.then(
        combinedClickable(
            onLongClick = longPress,
        ) {
            keyboardController?.hide()
            focusManager.clearFocus()
        }
    )
}


