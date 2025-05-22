package com.tommy.siliconflow.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tommy.siliconflow.app.extensions.navigateAndClearAll
import com.tommy.siliconflow.app.extensions.navigateAndPopBackStack
import com.tommy.siliconflow.app.extensions.safePopBackStack
import com.tommy.siliconflow.app.ui.compose.LoginScreen
import com.tommy.siliconflow.app.ui.compose.MainScreen
import com.tommy.siliconflow.app.ui.compose.ModelListScreen
import com.tommy.siliconflow.app.ui.compose.SplashScreen
import com.tommy.siliconflow.app.ui.compose.UserInfoScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    NavHost(navController, startDestination = Route.SPLASH_SCREEN) {
        composable(Route.SPLASH_SCREEN) {
            SplashScreen({
                navController.navigateAndPopBackStack(it)
            })
        }
        composable(Route.LOGIN_SCREEN) { LoginScreen({ navController.navigateAndPopBackStack(it) }) }
        composable(Route.MAIN_SCREEN) { MainScreen { navController.navigate(it) } }
        composable(Route.PERSONAL_INFO_SCREEN) {
            UserInfoScreen(
                popBack = { navController.safePopBackStack(scope) },
                logout = { navController.navigateAndClearAll(Route.LOGIN_SCREEN) }
            )
        }
        composable(Route.MODEL_LIST_SCREEN) {
            ModelListScreen({ navController.safePopBackStack(scope) })
        }
    }
}