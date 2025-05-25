package com.tommy.siliconflow.app.ui.theme

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

object CommonColor {
    @Stable
    val LightGray = Color(0xFFEEEEEE)

    @Stable
    val TinyLightGray = Color(0xFFF5F5F5)

    @Stable
    val LoadingBg = Color.Black.copy(alpha = 0.6f)

    @Stable
    val SettingLineBg = Color(0xFFEEEEEE)
}

object AppColor {
    val Transparent = Color.Transparent
    val White = PaletteTokens.White

    val Background = PaletteTokens.Pure252
    val BackgroundLevel1 = PaletteTokens.Pure242
    val Container = PaletteTokens.Pure231
    val CardContainer = PaletteTokens.White

    val PrimaryText = PaletteTokens.Pure6
    val SecondaryText = PaletteTokens.Pure127

    val Icon = PaletteTokens.White
    val IconContainer = PaletteTokens.Black
    val PopContainer = PaletteTokens.White

    val Highlight = PaletteTokens.Error
}

object AppDarkColor {
    val Background = PaletteTokens.Pure16
    val BackgroundLevel1 = PaletteTokens.Pure34
    val Container = PaletteTokens.Pure51
    val CardContainer = PaletteTokens.Pure28

    val PrimaryText = PaletteTokens.Pure243
    val SecondaryText = PaletteTokens.Pure160

    val Icon = PaletteTokens.Black
    val IconContainer = PaletteTokens.White
    val PopContainer = PaletteTokens.Pure50

    val Highlight = PaletteTokens.Error1
}

private object PaletteTokens {
    val Black = Color(red = 0, green = 0, blue = 0)
    val Pure6 = Color(red = 6, green = 6, blue = 6)
    val Pure16 = Color(red = 16, green = 16, blue = 16)
    val Pure28 = Color(red = 28, green = 28, blue = 28)
    val Pure34 = Color(red = 34, green = 34, blue = 34)
    val Pure50 = Color(red = 50, green = 50, blue = 50)
    val Pure51 = Color(red = 51, green = 51, blue = 51)
    val Pure77 = Color(red = 77, green = 77, blue = 77)
    val Pure127 = Color(red = 127, green = 127, blue = 127)
    val Pure160 = Color(red = 160, green = 160, blue = 160)
    val Pure183 = Color(red = 183, green = 183, blue = 183)
    val Pure231 = Color(red = 231, green = 231, blue = 231)
    val Pure242 = Color(red = 242, green = 242, blue = 242)
    val Pure243 = Color(red = 243, green = 243, blue = 243)
    val Pure252 = Color(red = 252, green = 252, blue = 252)
    val White = Color(red = 255, green = 255, blue = 255)

    val Error = Color(red = 244, green = 78, blue = 85)
    val Error1 = Color(red = 222, green = 96, blue = 104)
}