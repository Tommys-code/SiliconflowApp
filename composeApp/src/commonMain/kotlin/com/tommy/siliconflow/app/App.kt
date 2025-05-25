package com.tommy.siliconflow.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.data.isDark
import com.tommy.siliconflow.app.navigation.MainNavigation
import com.tommy.siliconflow.app.platform.LocalAppLocale
import com.tommy.siliconflow.app.platform.LocalAppTheme
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.ui.theme.darkCustomColorScheme
import com.tommy.siliconflow.app.ui.theme.lightCustomColorScheme
import com.tommy.siliconflow.app.viewmodel.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun App() {
    val viewModel = koinViewModel<AppViewModel>()
    AppEnvironment(viewModel) {
        MainNavigation()
    }
}

@Composable
private fun AppEnvironment(viewModel: AppViewModel, content: @Composable () -> Unit) {
    val settingOptions = viewModel.customAppLocale.collectAsStateWithLifecycle(null).value
    CompositionLocalProvider(
        LocalAppLocale provides settingOptions?.language?.value,
        LocalAppTheme provides settingOptions?.isDark
    ) {
        AppTheme(if (isSystemInDarkTheme()) darkCustomColorScheme() else lightCustomColorScheme()) {
            content()
        }
    }
}