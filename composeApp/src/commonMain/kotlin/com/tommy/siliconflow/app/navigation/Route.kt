package com.tommy.siliconflow.app.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class AppScreen {

    @Serializable
    data object Splash : AppScreen()

    @Serializable
    data object Login : AppScreen()

    @Serializable
    data object Main : AppScreen()

    @Serializable
    data object PersonalInfo : AppScreen()

    @Serializable
    data object ModelList : AppScreen()

    @Serializable
    data class ImageCreation(val sessionID: Long?) : AppScreen()

    @Serializable
    data object SettingGraph {
        @Serializable
        data object Setting : AppScreen()

        @Serializable
        data object LanguageSetting : AppScreen()

        @Serializable
        data object ThemeSetting : AppScreen()
    }
}
