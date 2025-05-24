package com.tommy.siliconflow.app.data

import org.jetbrains.compose.resources.StringResource
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.language_default
import siliconflowapp.composeapp.generated.resources.language_en
import siliconflowapp.composeapp.generated.resources.language_zh

data class SettingOptions(
    val language: Language = Language.DEFAULT,
)

enum class Language(val value: String?, val title: StringResource) {
    DEFAULT(null, Res.string.language_default),
    CHINESE("zh", Res.string.language_zh),
    ENGLISH("en", Res.string.language_en);

    companion object {
        fun valueOf(value: String?): Language {
            return entries.firstOrNull { it.value == value } ?: DEFAULT
        }
    }
}
