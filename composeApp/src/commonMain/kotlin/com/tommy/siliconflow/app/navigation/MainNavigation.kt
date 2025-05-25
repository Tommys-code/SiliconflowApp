package com.tommy.siliconflow.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.tommy.siliconflow.app.extensions.navigateAndClearAll
import com.tommy.siliconflow.app.extensions.navigateAndPopBackStack
import com.tommy.siliconflow.app.ui.compose.LoginScreen
import com.tommy.siliconflow.app.ui.compose.MainScreen
import com.tommy.siliconflow.app.ui.compose.ModelListScreen
import com.tommy.siliconflow.app.ui.compose.SplashScreen
import com.tommy.siliconflow.app.ui.compose.UserInfoScreen
import com.tommy.siliconflow.app.ui.compose.setting.LanguageSettingScreen
import com.tommy.siliconflow.app.ui.compose.setting.SettingScreen
import com.tommy.siliconflow.app.ui.compose.setting.ThemeScreen
import com.tommy.siliconflow.app.viewmodel.SettingViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = AppScreen.Splash,
        enterTransition = NavigationAnimation.enterTransition,
        exitTransition = NavigationAnimation.exitTransition,
        popEnterTransition = NavigationAnimation.popEnterTransition,
        popExitTransition = NavigationAnimation.popExitTransition,
    ) {
        composable<AppScreen.Splash> { SplashScreen({ navController.navigateAndPopBackStack(it) }) }
        composable<AppScreen.Login> {
            LoginScreen(
                { navController.navigate(it) },
                { navController.navigateAndPopBackStack(it) },
            )
        }
        composable<AppScreen.Main> { MainScreen { navController.navigate(it) } }
        composable<AppScreen.PersonalInfo> {
            UserInfoScreen(
                popBack = { navController.popBackStack() },
                logout = { navController.navigateAndClearAll(AppScreen.Login) }
            )
        }
        composable<AppScreen.ModelList> {
            ModelListScreen({ navController.popBackStack() })
        }
        settingNavigation(navController)
    }
}

private fun NavGraphBuilder.settingNavigation(navController: NavHostController) {
    navigation<AppScreen.SettingGraph>(startDestination = AppScreen.SettingGraph.Setting) {
        composable<AppScreen.SettingGraph.Setting> {
            SettingScreen(
                navigate = { navController.navigate(it) },
                popBack = { navController.popBackStack() },
            )
        }
        composable<AppScreen.SettingGraph.LanguageSetting> {
            val settingViewModel = koinViewModel<SettingViewModel>(
                viewModelStoreOwner = navController.getBackStackEntry(AppScreen.SettingGraph.Setting)
            )
            LanguageSettingScreen(settingViewModel) { navController.popBackStack() }
        }
        composable<AppScreen.SettingGraph.ThemeSetting> {
            val settingViewModel = koinViewModel<SettingViewModel>(
                viewModelStoreOwner = navController.getBackStackEntry(AppScreen.SettingGraph.Setting)
            )
            ThemeScreen(settingViewModel) { navController.popBackStack() }
        }
    }
}
