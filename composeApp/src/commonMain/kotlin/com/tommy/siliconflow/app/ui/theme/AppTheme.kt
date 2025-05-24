package com.tommy.siliconflow.app.ui.theme

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
}