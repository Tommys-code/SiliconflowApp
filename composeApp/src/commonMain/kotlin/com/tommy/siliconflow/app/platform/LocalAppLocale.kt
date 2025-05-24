package com.tommy.siliconflow.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object LocalAppLocale {
    val current: String @Composable get
    @Composable infix fun provides(value: String?): ProvidedValue<*>
}
