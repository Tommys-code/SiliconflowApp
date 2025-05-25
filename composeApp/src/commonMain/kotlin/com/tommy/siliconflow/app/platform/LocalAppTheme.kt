package com.tommy.siliconflow.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

expect object LocalAppTheme {
    val current: Boolean @Composable get
    @Composable infix fun provides(value: Boolean?): ProvidedValue<*>
}