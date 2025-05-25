package com.tommy.siliconflow.app.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tommy.siliconflow.app.ui.theme.AppTheme
import com.tommy.siliconflow.app.viewmodel.SplashViewEvent
import com.tommy.siliconflow.app.viewmodel.SplashViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import siliconflowapp.composeapp.generated.resources.Res
import siliconflowapp.composeapp.generated.resources.ic_silicon_flow

@Composable
internal fun SplashScreen(
    onNavigate: (route: Any) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = koinViewModel(),
) {
    Scaffold {
        Box(
            modifier = modifier.fillMaxSize().background(AppTheme.colorScheme.container),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_silicon_flow),
                contentDescription = "logo",
            )
        }
    }
    viewModel.viewEvent.collectAsState(null).value?.let {
        when (it) {
            is SplashViewEvent.Navigate -> onNavigate.invoke(it.route)
        }
    }
}
