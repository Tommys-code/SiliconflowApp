package com.tommy.siliconflow.app.platform

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object LocalAppLocale {
    @SuppressLint("ConstantLocale")
    private val default: Locale = Locale.getDefault()
    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        val context = LocalContext.current

        val newConfig = Configuration(configuration)
        val newLocale = when (value) {
            null -> default
            else -> Locale(value)
        }
        Locale.setDefault(newLocale)
        newConfig.setLocale(newLocale)
        context.createConfigurationContext(newConfig)
        return LocalConfiguration.provides(newConfig)
    }
}