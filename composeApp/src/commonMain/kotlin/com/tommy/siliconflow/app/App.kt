package com.tommy.siliconflow.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tommy.siliconflow.app.navigation.MainNavigation
import com.tommy.siliconflow.app.platform.LocalAppLocale
import com.tommy.siliconflow.app.viewmodel.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun App() {
    val viewModel = koinViewModel<AppViewModel>()
    AppEnvironment(viewModel) {
        MaterialTheme {
            MainNavigation()
        }
    }
}

@Composable
private fun AppEnvironment(viewModel: AppViewModel, content: @Composable () -> Unit) {
    val settingOptions = viewModel.customAppLocale.collectAsStateWithLifecycle(null).value
    CompositionLocalProvider(
        LocalAppLocale provides settingOptions?.language?.value,
    ) {
        key(settingOptions?.language) {
            content()
        }
    }
}