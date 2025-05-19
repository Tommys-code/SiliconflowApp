package com.tommy.siliconflow.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.tommy.siliconflow.app.navigation.MainNavigation

@Composable
internal fun App() {
    MaterialTheme {
        MainNavigation()
    }
}