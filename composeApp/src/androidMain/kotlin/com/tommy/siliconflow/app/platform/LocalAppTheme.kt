package com.tommy.siliconflow.app.platform

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.app.ComponentActivity
import androidx.core.view.WindowCompat

actual object LocalAppTheme {
    actual val current: Boolean
        @Composable get() = (LocalConfiguration.current.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES

    @Composable
    actual infix fun provides(value: Boolean?): ProvidedValue<*> {
        val new = if (value == null) {
            SystemBarColorChange(current)
            LocalConfiguration.current
        } else {
            SystemBarColorChange(value)
            Configuration(LocalConfiguration.current).apply {
                uiMode = when (value) {
                    true -> (uiMode and UI_MODE_NIGHT_MASK.inv()) or UI_MODE_NIGHT_YES
                    false -> (uiMode and UI_MODE_NIGHT_MASK.inv()) or UI_MODE_NIGHT_NO
                }
            }
        }
        return LocalConfiguration.provides(new)
    }

    @Composable
    @SuppressLint("RestrictedApi", "ContextCastToActivity")
    private fun SystemBarColorChange(isDarkMode: Boolean) {
        val window = (LocalContext.current as? ComponentActivity)?.window ?: return
        val view = LocalView.current
        SideEffect {
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkMode
        }
    }
}