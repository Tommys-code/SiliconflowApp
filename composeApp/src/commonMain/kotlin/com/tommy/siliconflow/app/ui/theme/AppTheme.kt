package com.tommy.siliconflow.app.ui.theme

import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun AppTheme(
    customColorScheme: CustomColorScheme = AppTheme.colorScheme,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalCustomColorScheme provides customColorScheme) {
        MaterialTheme(colorScheme = customColorScheme.colorScheme) {
            content()
        }
    }
}

object AppTheme {
    val colorScheme: CustomColorScheme
        @Composable @ReadOnlyComposable get() = LocalCustomColorScheme.current

    val checkBoxDefaultColor: CheckboxColors
        @Composable get() = CheckboxDefaults.colors(
            checkedColor = colorScheme.iconContainer,
        )
}