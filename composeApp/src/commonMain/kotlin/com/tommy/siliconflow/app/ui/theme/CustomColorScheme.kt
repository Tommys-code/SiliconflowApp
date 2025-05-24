package com.tommy.siliconflow.app.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalCustomColorScheme = staticCompositionLocalOf { lightCustomColorScheme() }

@Immutable
class CustomColorScheme(
    val sendChatContainer: Color,
    val colorScheme: ColorScheme,
)

fun lightCustomColorScheme(
    sendChatContainer: Color = Color.Black
) = CustomColorScheme(
    sendChatContainer = sendChatContainer,
    colorScheme = lightColorScheme()
)

fun darkCustomColorScheme(
    sendChatContainer: Color = Color.White
) = CustomColorScheme(
    sendChatContainer = sendChatContainer,
    colorScheme = darkColorScheme()
)
