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
    val backgroundLeve1: Color,
    val container: Color,
    val cardContainer: Color,
    val popContainer: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val icon: Color,
    val iconContainer: Color,
    val iconContainerDisable: Color = Color.LightGray,
    val highlight: Color,
    val colorScheme: ColorScheme,
)

fun lightCustomColorScheme(
    backgroundLeve1: Color = AppColor.BackgroundLevel1,
    container: Color = AppColor.Container,
    cardContainer: Color = AppColor.CardContainer,
    popContainer: Color = AppColor.PopContainer,
    primaryText: Color = AppColor.PrimaryText,
    secondaryText: Color = AppColor.SecondaryText,
    icon: Color = AppColor.Icon,
    iconContainer: Color = AppColor.IconContainer,
    highlight: Color = AppColor.Highlight,
) = CustomColorScheme(
    backgroundLeve1 = backgroundLeve1,
    container = container,
    cardContainer = cardContainer,
    popContainer = popContainer,
    primaryText = primaryText,
    secondaryText = secondaryText,
    icon = icon,
    iconContainer = iconContainer,
    highlight = highlight,
    colorScheme = lightColorScheme(
        background = AppColor.Background,
        surface = AppColor.Background,
        surfaceContainerHighest = cardContainer,
    )
)

fun darkCustomColorScheme(
    backgroundLeve1: Color = AppDarkColor.BackgroundLevel1,
    container: Color = AppDarkColor.Container,
    cardContainer: Color = AppDarkColor.CardContainer,
    popContainer: Color = AppDarkColor.PopContainer,
    primaryText: Color = AppDarkColor.PrimaryText,
    secondaryText: Color = AppDarkColor.SecondaryText,
    icon: Color = AppDarkColor.Icon,
    iconContainer: Color = AppDarkColor.IconContainer,
    highlight: Color = AppDarkColor.Highlight,
) = CustomColorScheme(
    backgroundLeve1 = backgroundLeve1,
    container = container,
    cardContainer = cardContainer,
    popContainer = popContainer,
    primaryText = primaryText,
    secondaryText = secondaryText,
    icon = icon,
    iconContainer = iconContainer,
    highlight = highlight,
    colorScheme = darkColorScheme(
        background = AppDarkColor.Background,
        surface = AppDarkColor.Background,
        surfaceContainerHighest = cardContainer,
    )
)
